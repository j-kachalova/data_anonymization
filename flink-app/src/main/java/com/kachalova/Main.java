package com.kachalova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.dto.AnonymizedFieldDto;
import com.kachalova.dto.RequestDto;
import com.kachalova.dto.ResponseDto;
import com.kachalova.strategy.impl.EmailTransformStrategy;
import com.kachalova.strategy.impl.PhoneTransformStrategy;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1); // 2 TaskManager'а

        ObjectMapper objectMapper = new ObjectMapper();

        DataStream<String> inputStream = env.fromSource(
                KafkaSource.<String>builder()
                        .setBootstrapServers("kafka:9092")
                        .setTopics("make-anonymous")
                        .setGroupId("flink-anonymizer")
                        .setStartingOffsets(OffsetsInitializer.earliest())
                        .setValueOnlyDeserializer(new SimpleStringSchema())
                        .build(),
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );
        DataStream<RequestDto> dtoStream = inputStream
                .map(json -> objectMapper.readValue(json, RequestDto.class));

// Email stream
        DataStream<AnonymizedFieldDto> emailStream = dtoStream
                .map(dto -> new AnonymizedFieldDto(
                        dto.getId(),
                        "email",
                        new EmailTransformStrategy(true,
                                false,
                                EmailTransformStrategy.InvalidEmailAction.GENERATE,
                                EmailTransformStrategy.EmailType.UUID_V4,
                                50).anonymize(dto.getEmail())
                )).slotSharingGroup("email");

// Phone stream
        DataStream<AnonymizedFieldDto> phoneStream = dtoStream
                .map(dto -> new AnonymizedFieldDto(
                        dto.getId(),
                        "phone",
                        new PhoneTransformStrategy(
                                PhoneTransformStrategy.Mode.GENERATE,
                                PhoneTransformStrategy.Format.STRING,
                                false,
                                0,
                                null
                        ).anonymize(dto.getPhone())
                )).slotSharingGroup("phone");

// Объединяем потоки
        DataStream<AnonymizedFieldDto> merged = emailStream.union(phoneStream);

// Группируем по ID и собираем поля в ResponseDto
        DataStream<ResponseDto> responseStream = merged
                .keyBy(AnonymizedFieldDto::getId)
                .process(new ProcessFunction<AnonymizedFieldDto, ResponseDto>() {
                    private final Map<String, ResponseDto> buffer = new HashMap<>();

                    @Override
                    public void processElement(AnonymizedFieldDto value, Context ctx, Collector<ResponseDto> out) {
                        ResponseDto dto = buffer.getOrDefault(value.getId(), new ResponseDto());
                        dto.setId(value.getId());

                        if ("email".equals(value.getFieldType())) {
                            dto.setEmail(value.getValue());
                        } else if ("phone".equals(value.getFieldType())) {
                            dto.setPhone(value.getValue());
                        }

                        // если оба поля получены — выводим и убираем из буфера
                        if (dto.getEmail() != null && dto.getPhone() != null) {
                            out.collect(dto);
                            buffer.remove(value.getId());
                        } else {
                            buffer.put(value.getId(), dto);
                        }
                    }
                });
        // Сериализуем в JSON и направляем в Kafka
        responseStream
                .map(response -> objectMapper.writeValueAsString(response))
                .sinkTo(
                        KafkaSink.<String>builder()
                                .setBootstrapServers("kafka:9092")
                                .setRecordSerializer(
                                        KafkaRecordSerializationSchema.builder()
                                                .setTopic("result") // Название выходного топика
                                                .setValueSerializationSchema(new SimpleStringSchema())
                                                .build()
                                )
                                .build()
                );
        env.execute("Distributed Anonymization Job");
    }
}