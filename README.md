Dependencies:
Spring Web
Spring Security
Spring Data JPA
MySQL Driver
Lombok
Validation

auth-service/
 ├── controller/
 ├── service/
 ├── repository/
 ├── entity/
 ├── dto/
 ├── security/
 ├── jwt/
 ├── mfa/
 └── config/



CREATE DATABASE agrilink_auth;
@Column(unique = true)
private String email;

UPDATE users SET mfa_enabled = true WHERE user_id = 1;
SELECT email, mfa_enabled FROM users;
SELECT user_id, email, mfa_enabled FROM users;
DELETE FROM users WHERE user_id = 2;

