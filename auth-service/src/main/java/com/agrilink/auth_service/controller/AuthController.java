package com.agrilink.auth_service.controller;

import com.agrilink.auth_service.dto.LoginRequest;
import com.agrilink.auth_service.dto.OtpRequest;
import com.agrilink.auth_service.dto.RegisterRequest;
import com.agrilink.auth_service.entity.RefreshToken;
import com.agrilink.auth_service.entity.User;
import com.agrilink.auth_service.jwt.JwtUtil;
import com.agrilink.auth_service.mfa.MfaService;
import com.agrilink.auth_service.repository.UserRepository;
import com.agrilink.auth_service.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        userRepository.save(user);

        return ResponseEntity.ok("User registered");
    }

    // 🔹 LOGIN (WITH MFA + REFRESH TOKEN)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 🔐 MFA FLOW
        if (user.isMfaEnabled()) {

            String otp = mfaService.generateOtp(user.getUserId());

            System.out.println("OTP: " + otp); // simulate sending

            return ResponseEntity.ok("OTP sent");
        }

        // 🔄 TOKEN GENERATION
        RefreshToken refreshToken = refreshTokenService.createToken(user.getUserId());

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        ));
    }

    // 🔐 VERIFY OTP
    @PostMapping("/mfa/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean valid = mfaService.verifyOtp(user.getUserId(), request.getOtp());

        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        // 🔄 TOKEN GENERATION AFTER OTP
        RefreshToken refreshToken = refreshTokenService.createToken(user.getUserId());

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        ));
    }

    // 🔄 REFRESH TOKEN API
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {

        String refreshTokenValue = request.get("refreshToken");

        RefreshToken refreshToken = refreshTokenService.validateToken(refreshTokenValue);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }
}