package com.nexus.realestate.matching;

import com.nexus.realestate.model.User;
import com.nexus.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchScoringJob {

    private final MatchingService matchingService;
    private final UserRepository userRepository;

    // Τρέχει κάθε 1 ώρα (3.600.000 milliseconds)
    @Scheduled(fixedRate = 3600000)
    public void runBatchReScoring() {
        System.out.println("Εκκίνηση Batch Scoring...");
        List<User> buyers = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("BUYER"))
                .toList();

        for (User buyer : buyers) {
            matchingService.calculateScoresForUser(buyer);
        }
        System.out.println("Το Batch Scoring ολοκληρώθηκε επιτυχώς!");
    }
}