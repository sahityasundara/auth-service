package com.agrilink.auth_service.entity;




import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // 🔥 IMPORTANT
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String email;
    private String password;
    private String role;
    private String status;

    private boolean mfaEnabled;
    private LocalDateTime createdAt;
}
