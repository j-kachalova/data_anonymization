package com.kachalova.anonymizer.service.impl;

import com.kachalova.anonymizer.dto.RequestDto;
import com.kachalova.anonymizer.dto.ResponseDto;
import com.kachalova.anonymizer.service.AnonymizationService;
import org.springframework.stereotype.Service;

@Service
public class AnonymizationServiceImpl implements AnonymizationService{
    @Override
    public ResponseDto anonymize(RequestDto requestDto) {
        return null;
    }
}
