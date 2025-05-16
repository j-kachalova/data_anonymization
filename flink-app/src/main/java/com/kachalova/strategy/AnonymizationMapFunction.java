package com.kachalova.strategy;

import com.kachalova.dto.AnonymizedFieldDto;
import com.kachalova.dto.OriginalDataDto;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Objects;

public class AnonymizationMapFunction implements MapFunction<ConsumerRecord<String, OriginalDataDto>, AnonymizedFieldDto> {
    private final String type;
    private final AnonymizationStrategy strategy;

    public AnonymizationMapFunction(String type, AnonymizationStrategy strategy) {
        this.type = type;
        this.strategy = strategy;
    }

    @Override
    public AnonymizedFieldDto map(ConsumerRecord<String, OriginalDataDto> record) {
        if (record == null || record.value() == null || record.key() == null) {
            return null;
        }

        String value = StrategySelector.getValue(record.value(), type);
        if (value == null) {
            return null;
        }

        return new AnonymizedFieldDto(record.key(), type, strategy.anonymize(value));
    }
}
