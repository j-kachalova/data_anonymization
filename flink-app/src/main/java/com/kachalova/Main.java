package com.kachalova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.dto.AnonymizedDataDto;
import com.kachalova.dto.AnonymizedFieldDto;
import com.kachalova.dto.OriginalDataDto;
import com.kachalova.strategy.AnonymizationMapFunction;
import com.kachalova.strategy.AnonymizationStrategy;
import com.kachalova.strategy.impl.EmailTransformStrategy;
import com.kachalova.strategy.impl.PassportTransformStrategy;
import com.kachalova.strategy.impl.PhoneTransformStrategy;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        ObjectMapper objectMapper = new ObjectMapper();

        KafkaSource<ConsumerRecord<String, OriginalDataDto>> source = KafkaSource.<ConsumerRecord<String, OriginalDataDto>>builder()
                .setBootstrapServers("kafka:9092")
                .setTopics("original-data-topic")
                .setGroupId("flink-anonymizer")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new KafkaRecordDeserializationSchema<>() {
                    @Override
                    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<ConsumerRecord<String, OriginalDataDto>> out) {
                        try {
                            String key = new String(record.key(), StandardCharsets.UTF_8);
                            OriginalDataDto value = objectMapper.readValue(record.value(), OriginalDataDto.class);
                            out.collect(new ConsumerRecord<>("original-data-topic", 0, 0L, key, value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public TypeInformation<ConsumerRecord<String, OriginalDataDto>> getProducedType() {
                        return TypeInformation.of(new TypeHint<>() {
                        });
                    }
                })
                .build();

        DataStream<ConsumerRecord<String, OriginalDataDto>> inputStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );

        // Email stream
        DataStream<AnonymizedFieldDto> emailStream = stream("email", inputStream);
// passport stream
        DataStream<AnonymizedFieldDto> passportStream = stream("passport", inputStream);
        // Phone stream
        DataStream<AnonymizedFieldDto> phoneStream = stream("phone", inputStream);

        DataStream<AnonymizedFieldDto> merged = emailStream.union(phoneStream).union(passportStream);

        DataStream<AnonymizedDataDto> responseStream = merged
                .keyBy(AnonymizedFieldDto::getId)
                .process(new ProcessFunction<AnonymizedFieldDto, AnonymizedDataDto>() {
                    private final Map<String, AnonymizedDataDto> buffer = new HashMap<>();

                    @Override
                    public void processElement(AnonymizedFieldDto value, Context ctx, Collector<AnonymizedDataDto> out) {
                        AnonymizedDataDto dto = buffer.getOrDefault(value.getId(), new AnonymizedDataDto());
                        dto.setId(value.getId());

                        switch (value.getFieldType()) {
                            case "email" -> dto.setEmail(value.getValue());
                            case "phone" -> dto.setPhone(value.getValue());
                            case "passport" -> dto.setPassport(value.getValue());
                        }

                        // Проверка, все ли поля установлены
                        if (dto.getEmail() != null &&
                                dto.getPhone() != null &&
                                dto.getPassport() != null) {

                            out.collect(dto);
                            buffer.remove(value.getId());
                        } else {
                            buffer.put(value.getId(), dto);
                        }
                    }
                })
                .slotSharingGroup("merger") // 💡 третий TaskManager
                .setParallelism(1);

        responseStream
                .map(dto -> objectMapper.writeValueAsString(dto))
                .sinkTo(
                        KafkaSink.<String>builder()
                                .setBootstrapServers("kafka:9092")
                                .setRecordSerializer(
                                        KafkaRecordSerializationSchema.builder()
                                                .setTopic("result")
                                                .setValueSerializationSchema(new SimpleStringSchema())
                                                .build()
                                )
                                .build()
                )
                .setParallelism(1);

        env.execute("Kafka-based Distributed Anonymization Job");
    }

    public static DataStream<AnonymizedFieldDto> stream(String type, DataStream<ConsumerRecord<String, OriginalDataDto>> inputStream) {
        AnonymizationStrategy strategy = addStrategy(type);
        return inputStream
                .map(new AnonymizationMapFunction(type, strategy))
                .slotSharingGroup(type)
                .setParallelism(1);
    }

    public static AnonymizationStrategy addStrategy(String type) {
        AnonymizationStrategy strategy = switch (type) {
            case "email" -> new EmailTransformStrategy(true, false,
                    EmailTransformStrategy.InvalidEmailAction.GENERATE,
                    EmailTransformStrategy.EmailType.UUID_V4,
                    50);
            case "phone" -> new PhoneTransformStrategy(
                    PhoneTransformStrategy.Mode.GENERATE,
                    PhoneTransformStrategy.Format.STRING,
                    false,
                    0,
                    null
            );
            case "passport" -> new PassportTransformStrategy();
            default -> null;
        };
        return strategy;
    }
    public static String getValue(OriginalDataDto record, String type){
        String value = switch (type) {
            case "email" -> record.getEmail();
            case "phone" -> record.getPhone();
            case "passport" -> record.getPassport();
            default -> null;
        };
        return value;
    }
}
