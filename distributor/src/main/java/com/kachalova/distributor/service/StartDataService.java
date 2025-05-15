package com.kachalova.distributor.service;


import com.kachalova.distributor.dao.entity.StartData;
import com.kachalova.distributor.web.dto.RequestDto;

public interface StartDataService {
    public StartData addStartData(RequestDto requestDto);
}
