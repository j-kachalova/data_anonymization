package com.kachalova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.dto.AnonymizedDataDto;
import com.kachalova.dto.AnonymizedFieldDto;
import com.kachalova.dto.OriginalDataDto;
import com.kachalova.strategy.AnonymizationMapFunction;
import com.kachalova.strategy.AnonymizationStrategy;
import com.kachalova.strategy.StrategySelector;
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
import java.util.Objects;

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


        DataStream<AnonymizedFieldDto> emailStream = stream("email", inputStream);

        DataStream<AnonymizedFieldDto> passportStream = stream("passport", inputStream);

        DataStream<AnonymizedFieldDto> phoneStream = stream("phone", inputStream);

        DataStream<AnonymizedFieldDto> addressStream = stream("address", inputStream);

        DataStream<AnonymizedFieldDto> birthDateStream = stream("birthDate", inputStream);

        DataStream<AnonymizedFieldDto> birthPlaceStream = stream("birthPlace", inputStream);
        DataStream<AnonymizedFieldDto> cardStream = stream("card", inputStream);

        DataStream<AnonymizedFieldDto> innStream = stream("inn", inputStream);

        DataStream<AnonymizedFieldDto> snilsStream = stream("snils", inputStream);




        DataStream<AnonymizedFieldDto> merged = emailStream.union(phoneStream)
                .union(passportStream)
                .union(addressStream)
                .union(birthDateStream)
                .union(birthPlaceStream)
                .union(cardStream)
                .union(innStream)
                .union(snilsStream);

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
                            case "birthDate" -> dto.setBirthDate(value.getValue());
                            case "birthPlace" -> dto.setBirthPlace(value.getValue());
                            case "address" -> dto.setAddress(value.getValue());
                            case "inn" -> dto.setInn(value.getValue());
                            case "snils" -> dto.setSnils(value.getValue());
                            case "card" -> dto.setCard(value.getValue());
                        }

                        // Проверка, все ли поля установлены
                        if (dto.getEmail() != null &&
                                dto.getPhone() != null &&
                                dto.getPassport() != null &&
                                dto.getBirthDate() != null &&
                                dto.getBirthPlace() != null &&
                                dto.getAddress() != null &&
                                dto.getInn() != null &&
                                dto.getSnils() != null &&
                                dto.getCard() != null) {

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
                .map(dto -> {
                    String json = objectMapper.writeValueAsString(dto);
                    System.out.println("Отправка в Kafka: " + json);
                    return json;
                })
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
        AnonymizationStrategy strategy = StrategySelector.addStrategy(type);
        return inputStream
                .map(new AnonymizationMapFunction(type, strategy))
                .filter(Objects::nonNull) //  фильтрация null-значений
                .slotSharingGroup(type)
                .setParallelism(1);
    }


}
