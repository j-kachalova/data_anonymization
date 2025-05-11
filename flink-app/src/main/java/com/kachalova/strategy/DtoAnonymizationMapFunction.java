package com.kachalova.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.dto.RequestDto;
import com.kachalova.strategy.impl.EmailAnonymizationStrategy;
import com.kachalova.strategy.impl.PhoneAnonymizationStrategy;
import org.apache.flink.api.common.functions.MapFunction;

public class DtoAnonymizationMapFunction implements MapFunction<String, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String map(String json) throws Exception {
        RequestDto dto = mapper.readValue(json, RequestDto.class);

        AnonymizationStrategy emailStrategy = new EmailAnonymizationStrategy();
        AnonymizationStrategy phoneStrategy = new PhoneAnonymizationStrategy();

        String id = dto.getId();
        String anonymizedEmail = dto.getEmail() != null ? emailStrategy.anonymize(dto.getEmail()) : null;
        String anonymizedPhone = dto.getPhone() != null ? phoneStrategy.anonymize(dto.getPhone()) : null;

        RequestDto anonymizedDto = new RequestDto(id, anonymizedPhone, anonymizedEmail);
        return mapper.writeValueAsString(anonymizedDto);
    }
}