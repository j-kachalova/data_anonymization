package com.kachalova.distributor.mapper;

import com.kachalova.distributor.dao.entity.OriginalData;

import com.kachalova.distributor.web.dto.OriginalDataDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class OriginalDataMapperTest {

    private final OriginalDataMapper mapper = Mappers.getMapper(OriginalDataMapper.class);

    @Test
    void shouldMapDtoToEntity() {
        OriginalDataDto dto = OriginalDataDto.builder()
                .email("test@example.com")
                .phone("1234567890")
                .build();

        OriginalData entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getPhone()).isEqualTo("1234567890");
    }

    @Test
    void shouldMapEntityToDto() {
        OriginalData entity = OriginalData.builder()
                .email("user@test.com")
                .phone("88005553535")
                .build();

        OriginalDataDto dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getPhone()).isEqualTo("88005553535");
    }
}
