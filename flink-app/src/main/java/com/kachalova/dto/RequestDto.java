package com.kachalova.dto;



public class RequestDto {
    String message;
    String message2;
    public RequestDto() {

    }

    public RequestDto(String message, String message2) {
        this.message = message;
        this.message2 = message2;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage2() {
        return message2;
    }
}
