package com.kachalova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.deserializer.JSONValueDeserializationSchema;
import com.kachalova.dto.RequestDto;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        ObjectMapper objectMapper = new ObjectMapper();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("kafka:9092")
                .setTopics("make-anonymous")
                .setGroupId("flink-anonymizer")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> inputStream = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
        );

        inputStream.map(json -> {
                    RequestDto dto = objectMapper.readValue(json, RequestDto.class);
                    return "Received message: " + dto.getMessage();
                })
                .print();

        env.execute("Anonymization Job");
    }
}