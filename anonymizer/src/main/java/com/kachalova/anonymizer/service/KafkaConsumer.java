package com.kachalova.anonymizer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {


    @KafkaListener(topics = "make-anonymous", groupId = "myGroup")
    public void makeAnonymous(String message) throws JsonProcessingException{
        log.info("KafkaConsumer finishListener: message:{}", message);
    }
}
