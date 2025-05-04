package com.kachalova;

import com.kachalova.deserializer.JSONValueDeserializationSchema;
import com.kachalova.dto.RequestDto;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String topic = "make-anonymous";
        KafkaSource<RequestDto> source = KafkaSource.<RequestDto>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics(topic)
                .setGroupId("myGroup")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new JSONValueDeserializationSchema())
                .build();
        DataStream<RequestDto> transactionStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka source");

        transactionStream.print();
        env.execute("Flink Ecommerce Realtime Streaming");
    }
}