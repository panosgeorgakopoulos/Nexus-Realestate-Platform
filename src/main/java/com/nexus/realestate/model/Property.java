package com.nexus.realestate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexus.realestate.enums.ListingStatus;
import com.nexus.realestate.enums.PropertyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Getter
@Setter
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    // Επιτρέπει στον Jackson να κάνει serialize το LAZY object χωρίς να κρασάρει
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ListingStatus status;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal sqm;

    private Integer floor;
    private Integer rooms;
    private Integer bathrooms;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(precision = 10, scale = 8)
    private BigDecimal lat;

    @Column(precision = 11, scale = 8)
    private BigDecimal lng;

    @Column(length = 300)
    private String address;

    @Column(name = "area_name", length = 100)
    private String areaName;

    @Column(nullable = false)
    private boolean approved = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}