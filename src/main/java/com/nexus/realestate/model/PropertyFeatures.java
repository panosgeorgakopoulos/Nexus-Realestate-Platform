package com.nexus.realestate.model;

import com.nexus.realestate.enums.HeatingType;
import com.nexus.realestate.enums.EnergyClass;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "property_features")
@Data
public class PropertyFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID χαρακτηριστικών

    // Σύνδεση 1-προς-1 με το ακίνητο (property_id FK -> properties.id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "has_parking")
    private boolean hasParking; // Πάρκινγκ

    @Column(name = "has_storage")
    private boolean hasStorage; // Αποθήκη

    @Column(name = "has_elevator")
    private boolean hasElevator; // Ασανσέρ

    @Column(name = "has_balcony")
    private boolean hasBalcony; // Μπαλκόνι

    @Enumerated(EnumType.STRING)
    @Column(name = "heating_type")
    private HeatingType heatingType; // Τύπος θέρμανσης

    @Enumerated(EnumType.STRING)
    @Column(name = "energy_class")
    private EnergyClass energyClass; // Ενεργειακή κλάση

    private boolean furnished; // Επιπλωμένο ή όχι

    @Column(name = "pets_allowed")
    private boolean petsAllowed; // Επιτρέπονται κατοικίδια
}
