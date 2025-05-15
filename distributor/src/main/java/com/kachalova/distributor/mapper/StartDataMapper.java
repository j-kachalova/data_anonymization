package com.kachalova.distributor.mapper;

import com.kachalova.distributor.dao.entity.StartData;
import com.kachalova.distributor.web.dto.RequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StartDataMapper {
    StartData requestDtoToStartData(RequestDto dto);

    RequestDto startDatatoRequestDto(StartData entity);
}

