package com.kachalova.distributor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kachalova.distributor.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/anonymize")
@RequiredArgsConstructor
public class AnonymizationController {
    private final KafkaProducer kafkaProducer;
    @PostMapping("")
    public void anonymize() throws JsonProcessingException {
       kafkaProducer.sendMessage();
    }
}
