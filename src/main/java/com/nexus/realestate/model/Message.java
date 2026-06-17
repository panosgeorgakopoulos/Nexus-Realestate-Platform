package com.nexus.realestate.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // Αποστολέας

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // Παραλήπτης (ιδιοκτήτης)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property; // Για ποιο ακίνητο

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body; // Κείμενο μηνύματος

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // Έχει διαβαστεί

    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt; // Ημερομηνία αποστολής

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
}
