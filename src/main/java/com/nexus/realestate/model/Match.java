package com.nexus.realestate.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches", uniqueConstraints = {
        // Εξασφαλίζει ότι υπάρχει μόνο ένα σκορ για κάθε συνδυασμό χρήστη-ακινήτου
        @UniqueConstraint(columnNames = {"user_id", "property_id"})
})
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Ο χρήστης (BUYER)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property; // Το ακίνητο

    @Column(nullable = false)
    private Integer score; // Βαθμός αντιστοίχισης (0-100)

    @Column(name = "calculated_at", updatable = false)
    private LocalDateTime calculatedAt; // Πότε υπολογίστηκε

    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
    }
}
