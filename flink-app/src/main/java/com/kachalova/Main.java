package com.kachalova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.deserializer.JSONValueDeserializationSchema;
import com.kachalova.dto.AnonymizedFieldDto;
import com.kachalova.dto.RequestDto;
import com.kachalova.dto.ResponseDto;
import com.kachalova.strategy.AnonymizationMapFunction;
import com.kachalova.strategy.AnonymizationStrategy;
import com.kachalova.strategy.DtoAnonymizationMapFunction;
import com.kachalova.strategy.impl.EmailAnonymizationStrategy;
import com.kachalova.strategy.impl.EmailTransformStrategy;
import com.kachalova.strategy.impl.PhoneAnonymizationStrategy;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

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
                        new PhoneAnonymizationStrategy().anonymize(dto.getPhone())
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
        responseStream
                .map(objectMapper::writeValueAsString)
                .print();
        env.execute("Distributed Anonymization Job");
    }
}