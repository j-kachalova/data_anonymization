package com.kachalova.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.dto.OriginalDataDto;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class OriginalDataDtoDeserializationSchema implements DeserializationSchema<OriginalDataDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OriginalDataDto deserialize(byte[] message) throws IOException {
        return objectMapper.readValue(message, OriginalDataDto.class);
    }

    @Override
    public boolean isEndOfStream(OriginalDataDto nextElement) {
        return false;
    }

    @Override
    public TypeInformation<OriginalDataDto> getProducedType() {
        return TypeInformation.of(OriginalDataDto.class);
    }
}
