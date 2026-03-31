package com.agrilink.auth_service.mfa;


import com.agrilink.auth_service.entity.MfaOtp;
import com.agrilink.auth_service.repository.MfaOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class MfaService {

    @Autowired
    private MfaOtpRepository otpRepository;

    public String generateOtp(Long userId) {

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        MfaOtp mfaOtp = new MfaOtp();
        mfaOtp.setUserId(userId);
        mfaOtp.setOtpCode(otp);
        mfaOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        mfaOtp.setVerified(false);

        otpRepository.save(mfaOtp);

        return otp;
    }

    public boolean verifyOtp(Long userId, String otp) {

        MfaOtp latestOtp = otpRepository
                .findTopByUserIdOrderByOtpIdDesc(userId)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (latestOtp.isVerified()) return false;

        if (!latestOtp.getOtpCode().equals(otp)) return false;

        if (latestOtp.getExpiryTime().isBefore(LocalDateTime.now())) return false;

        latestOtp.setVerified(true);
        otpRepository.save(latestOtp);

        return true;
    }
}