package com.kachalova.distributor.web.controller;

import com.kachalova.distributor.dao.entity.AnonymizedData;
import com.kachalova.distributor.dao.entity.LinkTable;
import com.kachalova.distributor.dao.entity.OriginalData;
import com.kachalova.distributor.mapper.OriginalDataMapper;
import com.kachalova.distributor.service.AnonymizationService;
import com.kachalova.distributor.service.KafkaProducer;
import com.kachalova.distributor.web.dto.AnonymizedDataDto;
import com.kachalova.distributor.web.dto.OriginalDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/anonymization")
@RequiredArgsConstructor
public class AnonymizationController {
    private final KafkaProducer kafkaProducer;
    private final AnonymizationService anonymizationService;
    private final OriginalDataMapper originalDataMapper;

    // 1. Добавление исходных данных
    @PostMapping("/original")
    public ResponseEntity<?> saveOriginal(@RequestBody OriginalDataDto dto) {
        OriginalData saved = anonymizationService.saveOriginal(dto);
        return ResponseEntity.ok(saved);
    }

    // 2. Запуск анонимизации по UUID
    @PostMapping("/run/{id}")
    public ResponseEntity<?> anonymize(@PathVariable UUID id) {
        OriginalData data = anonymizationService.findOriginalById(id);
        OriginalDataDto dto = originalDataMapper.toDto(data);
        kafkaProducer.sendOriginalDataDto(id, dto);
        return ResponseEntity.ok("Данные отправлены в Kafka для анонимизации");
    }
    @PostMapping("/anonymizeData")
    public ResponseEntity<?> anonymizeData(@RequestBody OriginalDataDto dto) {
        OriginalData data = anonymizationService.saveOriginal(dto);
        UUID id = data.getId();
        kafkaProducer.sendOriginalDataDto(id, dto);
        return ResponseEntity.ok("Данные отправлены в Kafka для анонимизации");
    }


    // 3. Получить обезличенные данные по анонимному ID
    @GetMapping("/anonymized/{anonymizedId}")
    public ResponseEntity<AnonymizedDataDto> getAnonymized(@PathVariable UUID anonymizedId) {
        return ResponseEntity.of(anonymizationService.getAnonymizedDto(anonymizedId));
    }

    // 4. (Опционально) Получить оригинальные данные по анонимному ID (деобезличивание)
    // Ограничить доступ через Spring Security
    @GetMapping("/deanonymize/{anonymizedId}")
    public ResponseEntity<OriginalDataDto> deanonymize(@PathVariable UUID anonymizedId) {
        return ResponseEntity.of(anonymizationService.getOriginalDtoByAnonymizedId(anonymizedId));
    }
    @GetMapping(("/all-original"))
    public ResponseEntity<List<OriginalData>> getAllOriginalData() {
        return ResponseEntity.ok(anonymizationService.getAllOriginalData());
    }
    @GetMapping("/all-link")
    public ResponseEntity<List<LinkTable>> getAllLinks() {
        return ResponseEntity.ok(anonymizationService.getAllLinks());
    }
    @GetMapping("/all-anonymized")
    public ResponseEntity<List<AnonymizedData>> getAllAnonymizedData() {
        return ResponseEntity.ok(anonymizationService.getAllAnonymizedData());
    }
    @GetMapping("/link/{id}")
    public ResponseEntity<LinkTable> getLinkById(@PathVariable UUID id) {
        return ResponseEntity.ok(anonymizationService.getLinkById(id));
    }

}
