package com.nexus.realestate.model;

import com.nexus.realestate.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data // Το Lombok δημιουργεί αυτόματα τους Getters/Setters
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Αυτόματο BIGSERIAL PK

    @Column(nullable = false, unique = true)
    private String email; // VARCHAR(255) UNIQUE

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // VARCHAR(255) για το BCrypt hash

    @Column(name = "full_name", length = 150)
    private String fullName; // VARCHAR(150)

    @Column(length = 20)
    private String phone; // VARCHAR(20)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // ENUM ρόλος χρήστη

    @Column(nullable = false)
    private boolean verified = false; // BOOLEAN DEFAULT false

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // TIMESTAMP DEFAULT NOW()

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}