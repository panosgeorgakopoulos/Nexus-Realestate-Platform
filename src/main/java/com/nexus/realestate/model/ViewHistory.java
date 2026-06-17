package com.nexus.realestate.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "views_history")
@Data
public class ViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Χρήστης που είδε

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property; // Ακίνητο που είδε

    @Column(name = "duration_sec")
    private Integer durationSec; // Χρόνος παραμονής σε δευτερόλεπτα

    @Column(nullable = false)
    private boolean dismissed = false; // Το απέρριψε ο χρήστης

    @Column(nullable = false)
    private boolean saved = false; // Το αποθήκευσε ο χρήστης

    @Column(name = "viewed_at", updatable = false)
    private LocalDateTime viewedAt; // Ημερομηνία προβολής

    @PrePersist
    protected void onCreate() {
        viewedAt = LocalDateTime.now();
    }
}
