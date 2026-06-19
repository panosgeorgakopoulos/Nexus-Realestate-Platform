package com.nexus.realestate.controller;

import com.nexus.realestate.model.Property;
import com.nexus.realestate.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties") // Όλα τα URLs αυτού του Controller θα ξεκινούν από εδώ
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    // 1. Λήψη όλων των αγγελιών (GET /api/properties)
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    // 2. Λήψη μιας αγγελίας με βάση το ID της (GET /api/properties/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    // 3. Δημιουργία νέας αγγελίας (POST /api/properties)
    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        Property createdProperty = propertyService.createProperty(property);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    // 4. Ενημέρωση μιας αγγελίας (PUT /api/properties/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property propertyDetails) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDetails));
    }

    // 5. Διαγραφή αγγελίας (DELETE /api/properties/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
