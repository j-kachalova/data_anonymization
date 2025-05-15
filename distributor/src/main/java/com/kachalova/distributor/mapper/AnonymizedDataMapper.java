package com.kachalova.distributor.mapper;

import com.kachalova.distributor.dao.entity.AnonymizedData;
import com.kachalova.distributor.web.dto.AnonymizedDataDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnonymizedDataMapper {
    AnonymizedDataDto toDto(AnonymizedData entity);
    AnonymizedData toEntity(AnonymizedDataDto dto);
}
