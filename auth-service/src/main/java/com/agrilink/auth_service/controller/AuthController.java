package com.agrilink.auth_service.controller;

import com.agrilink.auth_service.dto.LoginRequest;
import com.agrilink.auth_service.dto.OtpRequest;
import com.agrilink.auth_service.dto.RegisterRequest;
import com.agrilink.auth_service.entity.RefreshToken;
import com.agrilink.auth_service.entity.User;
import com.agrilink.auth_service.jwt.JwtUtil;
import com.agrilink.auth_service.mfa.MfaService;
import com.agrilink.auth_service.repository.UserRepository;
import com.agrilink.auth_service.service.AuthService;
import com.agrilink.auth_service.service.RefreshTokenService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.refresh(request.get("refreshToken")));
    }
}