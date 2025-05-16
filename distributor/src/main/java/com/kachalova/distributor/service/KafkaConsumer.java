package com.kachalova.distributor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.distributor.dao.entity.AnonymizedData;
import com.kachalova.distributor.dao.entity.LinkTable;
import com.kachalova.distributor.dao.repository.AnonymizedDataRepository;
import com.kachalova.distributor.dao.repository.LinkTableRepository;
import com.kachalova.distributor.dao.repository.OriginalRepo;
import com.kachalova.distributor.web.dto.AnonymizedDataDto;
import com.kachalova.distributor.web.dto.KafkaAnonymizedDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final AnonymizedDataRepository anonymizedDataRepository;
    private final OriginalRepo originalRepo;
    private final LinkTableRepository linkTableRepository;
    @KafkaListener(topics = "result", groupId = "myGroup")
    public void getResult(String message) throws JsonProcessingException {
        try {
            KafkaAnonymizedDataDto dto = objectMapper.readValue(message, KafkaAnonymizedDataDto.class);

            // Сохраняем обезличенные данные
            AnonymizedData anonymized = AnonymizedData.builder()
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .passport(dto.getPassport())
                    .build();

            anonymized = anonymizedDataRepository.save(anonymized);

            // Сохраняем связь
            LinkTable link = new LinkTable();
            link.setOriginalData(originalRepo.findById(UUID.fromString(dto.getId())).get());
            link.setAnonymizedData(anonymized);

            linkTableRepository.save(link);

            System.out.println("✅ Данные сохранены: " + dto+link);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
