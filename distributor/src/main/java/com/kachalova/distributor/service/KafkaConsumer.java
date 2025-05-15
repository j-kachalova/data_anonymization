package com.kachalova.distributor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.distributor.web.dto.AnonymizedDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper mapper;
    @KafkaListener(topics = "result", groupId = "myGroup")
    public void getResult(String message) throws JsonProcessingException {
        log.info("KafkaConsumer getResult: message:{}", message);
        AnonymizedDataDto anonymizedDataDto = mapper.readValue(message, AnonymizedDataDto.class);
        log.info("KafkaConsumer getResult: responseDto:{}", anonymizedDataDto);
    }
}
