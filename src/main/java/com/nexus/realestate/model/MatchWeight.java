package com.nexus.realestate.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "match_weights")
@Data
public class MatchWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String criterion; // Κριτήριο (π.χ. 'budget', 'location')

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal weight; // Βάρος ως ποσοστό (π.χ. 35.00)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy; // Admin που το άλλαξε

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Ημερομηνία τελευταίας αλλαγής

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
