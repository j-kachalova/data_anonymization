package com.kachalova.distributor.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kachalova.distributor.dao.entity.StartData;
import com.kachalova.distributor.service.AnonymizationService;
import com.kachalova.distributor.service.StartDataService;
import com.kachalova.distributor.web.dto.RequestDto;
import com.kachalova.distributor.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/anonymization")
@RequiredArgsConstructor
public class AnonymizationController {
    private final KafkaProducer kafkaProducer;
    private final StartDataService startDataService;
    private final AnonymizationService anonymizationService;
    // 1. Добавление исходных данных
    @PostMapping("/original")
    public ResponseEntity<?> saveOriginal(@RequestBody RequestDto requestDto) {
        StartData saved = anonymizationService.addStartData(requestDto);
        return ResponseEntity.ok(saved);
    }
    // 2. Запуск анонимизации по UUID
    @PostMapping("/run/{id}")
    public ResponseEntity<?> anonymize(@PathVariable UUID id) {
        anonymizationService.anonymize(id);
        return ResponseEntity.ok("Анонимизация завершена");
    }

    // 3. Получить обезличенные данные по анонимному ID
    @GetMapping("/anonymized/{anonymizedId}")
    public ResponseEntity<AnonymizedData> getAnonymized(@PathVariable String anonymizedId) {
        return ResponseEntity.of(anonymizationService.getAnonymized(anonymizedId));
    }

    // 4. (Опционально) Получить оригинальные данные по анонимному ID (деобезличивание)
    // Ограничить доступ через Spring Security
    @GetMapping("/deanonymize/{anonymizedId}")
    public ResponseEntity<OriginalData> deanonymize(@PathVariable String anonymizedId) {
        return ResponseEntity.of(anonymizationService.getOriginalByAnonymizedId(anonymizedId));
    }




  /*  @PostMapping("/anonymize")
    public void anonymize(@RequestBody RequestDto requestDto) throws JsonProcessingException {
       startDataService.addStartData(requestDto);
       kafkaProducer.sendMessage(requestDto);
    }*/
}
