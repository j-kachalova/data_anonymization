package com.kachalova.anonymizer.service;

import com.kachalova.anonymizer.dto.RequestDto;
import com.kachalova.anonymizer.dto.ResponseDto;

public interface AnonymizationService {
    public ResponseDto anonymize(RequestDto requestDto);
}
