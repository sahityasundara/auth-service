package com.agrilink.auth_service.repository;



import com.agrilink.auth_service.entity.MfaOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MfaOtpRepository extends JpaRepository<MfaOtp, Long> {

    Optional<MfaOtp> findTopByUserIdOrderByOtpIdDesc(Long userId);
}
