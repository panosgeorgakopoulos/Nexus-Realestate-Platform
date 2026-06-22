package com.nexus.realestate.controller;

import com.nexus.realestate.dto.SearchCriteria;
import com.nexus.realestate.model.Property;
import com.nexus.realestate.search.NLSearchParser;
import com.nexus.realestate.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final NLSearchParser nlSearchParser; // Η "γέφυρα" για τη φυσική γλώσσα

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    // Endpoint για Δυναμική Αναζήτηση - Συνδυάζει τον κώδικα του συνεργάτη σου με το NLP!
    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(@RequestParam(required = false) String query, SearchCriteria criteria) {
        // Αν το frontend έστειλε φυσική γλώσσα (π.χ. "διαμέρισμα 800 ευρώ")
        if (query != null && !query.isBlank()) {
            criteria = nlSearchParser.parse(query);
        }
        return ResponseEntity.ok(propertyService.searchProperties(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Property createdProperty = propertyService.createProperty(property, email);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property propertyDetails) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}