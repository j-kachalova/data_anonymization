package com.kachalova.distributor.mapper;

import com.kachalova.distributor.dao.entity.OriginalData;
import com.kachalova.distributor.web.dto.OriginalDataDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OriginalDataMapper {
    OriginalData toEntity(OriginalDataDto dto);

    OriginalDataDto toDto(OriginalData entity);
    List<OriginalDataDto> toDtoList(List<OriginalData> entities);
}

