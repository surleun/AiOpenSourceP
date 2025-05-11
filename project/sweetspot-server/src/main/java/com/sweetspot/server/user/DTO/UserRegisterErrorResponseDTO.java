package com.sweetspot.server.user.DTO;
// package: com.sweetspot.server.user.DTO

public class UserRegisterErrorResponseDTO {
    private String error;
    private String field; // "email" 또는 "phoneNumber"

    public UserRegisterErrorResponseDTO(String error, String field) {
        this.error = error;
        this.field = field;
    }

    public String getError() { return error; }

    public String getField() { return field; }
}
