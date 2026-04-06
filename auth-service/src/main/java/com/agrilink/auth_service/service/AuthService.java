package com.agrilink.auth_service.service;

import com.agrilink.auth_service.dto.LoginRequest;
import com.agrilink.auth_service.dto.OtpRequest;
import com.agrilink.auth_service.dto.RegisterRequest;
import com.agrilink.auth_service.entity.RefreshToken;
import com.agrilink.auth_service.entity.User;
import com.agrilink.auth_service.jwt.JwtUtil;
import com.agrilink.auth_service.mfa.MfaService;
import com.agrilink.auth_service.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MfaService mfaService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // 🔹 REGISTER
    public String register(RegisterRequest request) {

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        userRepository.save(user);

        return "User registered";
    }

    // 🔹 LOGIN
    public Object login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.isMfaEnabled()) {
            String otp = mfaService.generateOtp(user.getUserId(), user.getEmail());

            return "OTP sent";
        }

        RefreshToken refreshToken = refreshTokenService.createToken(user.getUserId());

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );
    }

    // 🔐 VERIFY OTP
    public Object verifyOtp(OtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean valid = mfaService.verifyOtp(user.getUserId(), request.getOtp());

        if (!valid) {
            throw new RuntimeException("Invalid OTP");
        }

        RefreshToken refreshToken = refreshTokenService.createToken(user.getUserId());

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        );
    }

    // 🔄 REFRESH
    public Object refresh(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenService.validateToken(refreshTokenValue);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return Map.of(
                "accessToken", newAccessToken
        );
    }
}