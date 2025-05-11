package com.kachalova.distributor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.distributor.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendMessage() throws JsonProcessingException {
        RequestDto requestDto = RequestDto.builder().id("kguyiviy").phone("88005553535").email("john.doe@example.com").build();
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);
        log.info("KafkaProducer sendMessage jsonRequestDto:{}",jsonRequestDto);
        kafkaTemplate.send("make-anonymous", jsonRequestDto);
    }



}
