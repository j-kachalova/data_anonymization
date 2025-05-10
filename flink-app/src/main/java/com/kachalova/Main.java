package com.kachalova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.deserializer.JSONValueDeserializationSchema;
import com.kachalova.dto.RequestDto;
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

        // Side outputs для message и message2
        final OutputTag<String> message1Tag = new OutputTag<String>("message1") {};
        final OutputTag<String> message2Tag = new OutputTag<String>("message2") {};


        SingleOutputStreamOperator<String> mainStream = inputStream.rebalance().process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
                RequestDto dto = objectMapper.readValue(value, RequestDto.class);
                ctx.output(message1Tag, dto.getMessage());
                ctx.output(message2Tag, dto.getMessage2());
            }
        });


        mainStream.getSideOutput(message1Tag)
                .map(x -> "TaskManager 1: " + x)
                .slotSharingGroup("group1")
                .setParallelism(1)
                .print();

        mainStream.getSideOutput(message2Tag)
                .map(x -> "TaskManager 2: " + x)
                .slotSharingGroup("group2")
                .setParallelism(1)
                .print();


        env.execute("Parallel Kafka Message Split Job");
    }
}