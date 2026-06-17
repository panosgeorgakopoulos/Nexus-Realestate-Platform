package com.nexus.realestate.model;

import com.nexus.realestate.enums.LifestyleType;
import com.nexus.realestate.enums.ListingStatus;
import com.nexus.realestate.enums.PropertyType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "preferences")
@Data
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Αυτόματο ID

    // Κάθε χρήστης (BUYER) έχει ένα προφίλ προτιμήσεων
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "budget_min", precision = 12, scale = 2)
    private BigDecimal budgetMin; // Ελάχιστο budget

    @Column(name = "budget_max", precision = 12, scale = 2)
    private BigDecimal budgetMax; // Μέγιστο budget

    @Column(name = "area_names", columnDefinition = "TEXT")
    private String areaNames; // Comma-separated περιοχές (π.χ. 'Γλυφάδα, Βούλα')

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type")
    private PropertyType propertyType; // Τύπος που ενδιαφέρει (Από το Enum που ήδη φτιάξαμε)

    @Column(name = "sqm_min", precision = 8, scale = 2)
    private BigDecimal sqmMin; // Ελάχιστα τ.μ.

    @Column(name = "rooms_min")
    private Integer roomsMin; // Ελάχιστα υπνοδωμάτια

    @Enumerated(EnumType.STRING)
    @Column(name = "lifestyle_type")
    private LifestyleType lifestyleType; // Lifestyle

    @Column(name = "needs_parking")
    private boolean needsParking; // Επιθυμεί πάρκινγκ

    @Column(name = "needs_elevator")
    private boolean needsElevator; // Επιθυμεί ασανσέρ

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status")
    private ListingStatus listingStatus; // Αγορά ή ενοικίαση (Από το Enum που ήδη φτιάξαμε)
}
