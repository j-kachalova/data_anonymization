package com.kachalova.anonymizer.service;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class FlinkAnonymizationService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaGroupId;

  /*  @Value("${spring.kafka.input-topic}")
    private String inputTopic;

    @Value("${spring.kafka.output-topic}")
    private String outputTopic;*/

    private final KafkaTemplate<String, String> kafkaTemplate;

    public FlinkAnonymizationService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Запуск Flink Job
    public void startFlinkJob() throws Exception {
        // Настройка среды выполнения Flink
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Настройка Kafka Consumer
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", kafkaBootstrapServers);
        properties.setProperty("group.id", kafkaGroupId);

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "make-anonymous", new SimpleStringSchema(), properties);

        // Поток данных из Kafka
        DataStream<String> rawDataStream = env.addSource(consumer);

        // Применение анонимизации
        DataStream<String> anonymizedStream = rawDataStream.map(this::anonymizeData);

        // Запись анонимизированных данных обратно в Kafka
     /*   FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(
                outputTopic, new SimpleStringSchema(), properties);
        anonymizedStream.addSink(producer);*/

        // Запуск задачи Flink
        env.execute("Flink Kafka Anonymization Job");
    }

    // Простой алгоритм анонимизации данных
    private String anonymizeData(String message) {
        // Пример анонимизации: замена всех цифр на "XXXX"
        return message.replaceAll("\\d{10}", "XXXX");
    }
}



