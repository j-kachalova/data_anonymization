package com.kachalova.distributor.service.impl;

import com.kachalova.distributor.dao.entity.StartData;
import com.kachalova.distributor.dao.repository.StartDataRepo;
import com.kachalova.distributor.mapper.StartDataMapper;
import com.kachalova.distributor.service.StartDataService;
import com.kachalova.distributor.web.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartDataServiceImpl implements StartDataService {
    private final StartDataRepo startDataRepo;
    private final StartDataMapper mapper;
    @Override
    public StartData addStartData(RequestDto requestDto) {
        log.info("StartDataServiceImpl: addStartData requestDto: {}", requestDto);
        StartData startDataFromDB = startDataRepo.findByEmail(requestDto.getEmail());
        log.debug("StartDataServiceImpl: addStartData startDataFromDB: {}", startDataFromDB);
        if (startDataFromDB != null) {
            return startDataFromDB;
        }
        StartData startData = mapper.requestDtoToStartData(requestDto);
        startDataRepo.save(startData);
        log.info("StartDataServiceImpl: addStartData startData: {}", startData);
        return startData;
    }
}
