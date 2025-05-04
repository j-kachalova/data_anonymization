package com.kachalova.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kachalova.dto.RequestDto;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class JSONValueDeserializationSchema implements DeserializationSchema<RequestDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void open(InitializationContext context) throws Exception {
        DeserializationSchema.super.open(context);
    }

    @Override
    public RequestDto deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, RequestDto.class);
    }

    @Override
    public boolean isEndOfStream(RequestDto transaction) {
        return false;
    }

    @Override
    public TypeInformation<RequestDto> getProducedType() {
        return TypeInformation.of(RequestDto.class);
    }
}
