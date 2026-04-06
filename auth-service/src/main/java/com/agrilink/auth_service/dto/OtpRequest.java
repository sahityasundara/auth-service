package com.agrilink.auth_service.dto;


import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data

public class OtpRequest {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;
}