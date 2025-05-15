package com.kachalova.distributor.service;

import com.kachalova.distributor.web.dto.OriginalDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, OriginalDataDto> kafkaTemplate;

    private static final String TOPIC = "original-data-topic";

    public void sendOriginalDataDto(UUID id, OriginalDataDto dto) {
        kafkaTemplate.send(TOPIC, id.toString(), dto);
    }

}
