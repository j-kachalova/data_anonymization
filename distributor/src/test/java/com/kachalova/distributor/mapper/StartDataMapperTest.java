package com.kachalova.distributor.mapper;

import com.kachalova.distributor.dao.entity.StartData;
import com.kachalova.distributor.web.dto.RequestDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class StartDataMapperTest {

    private final StartDataMapper mapper = Mappers.getMapper(StartDataMapper.class);

    @Test
    void shouldMapDtoToEntity() {
        RequestDto dto = RequestDto.builder()
                .email("test@example.com")
                .phone("1234567890")
                .build();

        StartData entity = mapper.requestDtoToStartData(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getPhone()).isEqualTo("1234567890");
    }

    @Test
    void shouldMapEntityToDto() {
        StartData entity = StartData.builder()
                .email("user@test.com")
                .phone("88005553535")
                .build();

        RequestDto dto = mapper.startDatatoRequestDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getPhone()).isEqualTo("88005553535");
    }
}
