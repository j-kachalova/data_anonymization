package com.kachalova.distributor.mapper;

import com.kachalova.distributor.dao.entity.OriginalData;
import com.kachalova.distributor.web.dto.OriginalDataDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OriginalDataMapper {
    OriginalData toEntity(OriginalDataDto dto);

    OriginalDataDto toDto(OriginalData entity);
}

