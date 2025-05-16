package com.kachalova.distributor.service;

import com.kachalova.distributor.dao.entity.AnonymizedData;
import com.kachalova.distributor.dao.entity.LinkTable;
import com.kachalova.distributor.dao.entity.OriginalData;
import com.kachalova.distributor.dao.repository.AnonymizedDataRepository;
import com.kachalova.distributor.dao.repository.LinkTableRepository;
import com.kachalova.distributor.dao.repository.OriginalRepo;
import com.kachalova.distributor.mapper.AnonymizedDataMapper;
import com.kachalova.distributor.mapper.OriginalDataMapper;
import com.kachalova.distributor.web.dto.AnonymizedDataDto;
import com.kachalova.distributor.web.dto.OriginalDataDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnonymizationService {

    private final OriginalRepo originalRepo;
    private final OriginalDataMapper originalDataMapper;
    private final AnonymizedDataMapper anonymizedDataMapper;
    private final AnonymizedDataRepository anonymizedRepo;
    private final LinkTableRepository linkRepo;


    public OriginalData saveOriginal(OriginalDataDto originalDataDto) {
        log.info("AnonymizationService: saveOriginal requestDto: {}", originalDataDto);

        // Проверка по email
        Optional<OriginalData> byEmail = originalRepo.findByEmail(originalDataDto.getEmail());
        if (byEmail.isPresent()) {
            log.debug("AnonymizationService: найдено по email: {}", byEmail.get());
            return byEmail.get();
        }

        // Проверка по номеру телефона
        Optional<OriginalData> byPhone = originalRepo.findByPhone(originalDataDto.getPhone());
        if (byPhone.isPresent()) {
            log.debug("AnonymizationService: найдено по телефону: {}", byPhone.get());
            return byPhone.get();
        }

        // Проверка по паспорту
        Optional<OriginalData> byPassport = originalRepo.findByPassport(originalDataDto.getPassport());
        if (byPassport.isPresent()) {
            log.debug("AnonymizationService: найдено по паспорту: {}", byPassport.get());
            return byPassport.get();
        }

        // Если не найдено — сохраняем
        OriginalData startData = originalDataMapper.toEntity(originalDataDto);
        originalRepo.save(startData);
        log.info("AnonymizationService: сохранены новые данные: {}", startData);
        return startData;
    }


    public OriginalData findOriginalById(UUID id) {
        return originalRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Данные не найдены: " + id));
    }

    public Optional<AnonymizedDataDto> getAnonymizedDto(UUID anonymizedId) {
        return anonymizedRepo.findById(anonymizedId)
                .map(anonymizedDataMapper::toDto);
    }
    public Optional<OriginalDataDto> getOriginalDtoByAnonymizedId(UUID anonymizedId) {
        return linkRepo.findByAnonymizedData_Id(anonymizedId)
                .flatMap(link -> originalRepo.findById(link.getOriginalData().getId()))
                .map(originalDataMapper::toDto);
    }
    public List<OriginalData> getAllOriginalData() {
        List<OriginalData> data = originalRepo.findAll();
        return data;
    }
    public List<LinkTable> getAllLinks() {
        return linkRepo.findAll();
    }
    public List<AnonymizedData> getAllAnonymizedData() {
        return anonymizedRepo.findAll();
    }
}
