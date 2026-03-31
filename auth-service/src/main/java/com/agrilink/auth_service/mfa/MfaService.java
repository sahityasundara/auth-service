package com.agrilink.auth_service.mfa;


import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MfaService {

    public String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}