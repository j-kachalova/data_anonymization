package com.kachalova.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.MapFunction;

public class AnonymizationMapFunction implements MapFunction<String, String> {

    private final AnonymizationStrategy strategy;

    public AnonymizationMapFunction(AnonymizationStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String map(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        String value = node.get("value").asText();
        return strategy.anonymize(value);
    }
}

