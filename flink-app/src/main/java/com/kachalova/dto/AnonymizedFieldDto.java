package com.kachalova.dto;

public class AnonymizedFieldDto {
    public String id;
    public String fieldType; // "email" или "phone"
    public String value;

    public AnonymizedFieldDto(String id, String fieldType, String value) {
        this.id = id;
        this.fieldType = fieldType;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getValue() {
        return value;
    }
}
