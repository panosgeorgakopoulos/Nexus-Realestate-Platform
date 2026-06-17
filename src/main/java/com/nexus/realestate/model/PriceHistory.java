package com.nexus.realestate.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
@Data
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property; // Το ακίνητο

    @Column(name = "old_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal oldPrice; // Παλιά τιμή

    @Column(name = "new_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal newPrice; // Νέα τιμή

    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt; // Ημερομηνία αλλαγής

    @Column(name = "change_reason", length = 200)
    private String changeReason; // Προαιρετική αιτία αλλαγής

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
