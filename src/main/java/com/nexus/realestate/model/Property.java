package com.nexus.realestate.model;

import com.nexus.realestate.enums.ListingStatus;
import com.nexus.realestate.enums.PropertyType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Data
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Αυτόματο ID ακινήτου

    // Εδώ συνδέουμε το ακίνητο με τον ιδιοκτήτη του (owner_id FK -> users.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(length = 200, nullable = false)
    private String title; // Τίτλος αγγελίας

    @Column(columnDefinition = "TEXT")
    private String description; // Πλήρης περιγραφή

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type; // Τύπος ακινήτου

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ListingStatus status; // Πώληση ή ενοικίαση

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price; // Τιμή σε ευρώ

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal sqm; // Εμβαδόν σε τ.μ.

    private Integer floor; // Όροφος

    private Integer rooms; // Αριθμός υπνοδωματίων

    private Integer bathrooms; // Αριθμός μπάνιων

    @Column(name = "year_built")
    private Integer yearBuilt; // Έτος κατασκευής

    @Column(precision = 10, scale = 8)
    private BigDecimal lat; // Γεωγραφικό πλάτος (χάρτης)

    @Column(precision = 11, scale = 8)
    private BigDecimal lng; // Γεωγραφικό μήκος (χάρτης)

    @Column(length = 300)
    private String address; // Διεύθυνση (κείμενο)

    @Column(name = "area_name", length = 100)
    private String areaName; // Περιοχή (π.χ. Κολωνάκι)

    @Column(nullable = false)
    private boolean approved = false; // Έγκριση από admin

    @Column(nullable = false)
    private boolean active = true; // Ενεργή/ανενεργή αγγελία

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Ημερομηνία καταχώρησης

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
