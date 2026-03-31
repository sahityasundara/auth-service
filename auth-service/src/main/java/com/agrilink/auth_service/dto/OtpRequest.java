package com.agrilink.auth_service.dto;


import lombok.Data;

@Data
public class OtpRequest {

    private String email;
    private String otp;
}