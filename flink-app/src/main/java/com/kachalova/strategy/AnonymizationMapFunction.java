package com.kachalova.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.Main;
import com.kachalova.dto.AnonymizedFieldDto;
import com.kachalova.dto.OriginalDataDto;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class AnonymizationMapFunction implements MapFunction<ConsumerRecord<String, OriginalDataDto>, AnonymizedFieldDto> {
    private final String type;
    private final AnonymizationStrategy strategy;

    public AnonymizationMapFunction(String type, AnonymizationStrategy strategy) {
        this.type = type;
        this.strategy = strategy;
    }

    @Override
    public AnonymizedFieldDto map(ConsumerRecord<String, OriginalDataDto> record) {
        String value = Main.getValue(record.value(), type); // Вызов статического метода
        return new AnonymizedFieldDto(record.key(), type, strategy.anonymize(value));
    }
}

