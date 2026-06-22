package com.nexus.realestate.controller;

import com.nexus.realestate.model.Match;
import com.nexus.realestate.model.User;
import com.nexus.realestate.repository.UserRepository;
import com.nexus.realestate.matching.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/properties") // Ανήκει στα properties βάσει του οδηγού
@RequiredArgsConstructor
public class MatchController {

    private final MatchingService matchingService;
    private final UserRepository userRepository;

    @GetMapping("/recommended")
    public ResponseEntity<List<Match>> getRecommendedProperties() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Ο χρήστης δεν βρέθηκε"));

        // Προκαλούμε υπολογισμό τη στιγμή του request για να έχει πάντα φρέσκα δεδομένα
        matchingService.calculateScoresForUser(currentUser);

        // Επιστρέφουμε τις προτάσεις
        return ResponseEntity.ok(matchingService.getRecommendationsForUser(currentUser));
    }
}