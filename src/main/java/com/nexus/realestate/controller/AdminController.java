package com.nexus.realestate.controller;

import com.nexus.realestate.model.MatchWeight;
import com.nexus.realestate.model.Property;
import com.nexus.realestate.repository.MatchWeightRepository;
import com.nexus.realestate.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
// Εξασφαλίζει ότι ΜΟΝΟ χρήστες με ρόλο ADMIN έχουν πρόσβαση εδώ (σε συνδυασμό με το SecurityConfig)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PropertyRepository propertyRepository;
    private final MatchWeightRepository matchWeightRepository;

    // 1. Λήψη αγγελιών που εκκρεμούν
    @GetMapping("/properties/pending")
    public ResponseEntity<List<Property>> getPendingProperties() {
        return ResponseEntity.ok(propertyRepository.findByApprovedFalse());
    }

    // 2. Έγκριση Αγγελίας
    @PutMapping("/properties/{id}/approve")
    public ResponseEntity<String> approveProperty(@PathVariable Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Το ακίνητο δεν βρέθηκε"));

        property.setApproved(true);
        propertyRepository.save(property);
        return ResponseEntity.ok("Η αγγελία εγκρίθηκε επιτυχώς και είναι πλέον ορατή!");
    }

    // 3. Απόρριψη / Soft Delete Αγγελίας
    @PutMapping("/properties/{id}/reject")
    public ResponseEntity<String> rejectProperty(@PathVariable Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Το ακίνητο δεν βρέθηκε"));

        property.setActive(false); // Soft delete
        propertyRepository.save(property);
        return ResponseEntity.ok("Η αγγελία απορρίφθηκε.");
    }

    // 4. Προβολή τρεχόντων βαρών του Matching Engine
    @GetMapping("/matching-weights")
    public ResponseEntity<List<MatchWeight>> getMatchingWeights() {
        return ResponseEntity.ok(matchWeightRepository.findAll());
    }

    // 5. Ενημέρωση ενός βάρους (π.χ. αύξηση της σημασίας του budget)
    @PutMapping("/matching-weights/{id}")
    public ResponseEntity<MatchWeight> updateWeight(@PathVariable Long id, @RequestBody MatchWeight newWeightData) {
        MatchWeight weight = matchWeightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Το βάρος δεν βρέθηκε"));

        weight.setWeight(newWeightData.getWeight());
        return ResponseEntity.ok(matchWeightRepository.save(weight));
    }
}