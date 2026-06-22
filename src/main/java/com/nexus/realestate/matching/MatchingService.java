package com.nexus.realestate.matching;

import com.nexus.realestate.model.Match;
import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.model.Property;
import com.nexus.realestate.model.User;
import com.nexus.realestate.repository.MatchRepository;
import com.nexus.realestate.repository.PreferencesRepository;
import com.nexus.realestate.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingEngine engine;
    private final MatchRepository matchRepository;
    private final PreferencesRepository preferencesRepository;
    private final PropertyRepository propertyRepository;

    @Transactional
    public void calculateScoresForUser(User user) {
        Optional<Preferences> prefsOpt = preferencesRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(user.getId())).findFirst();

        if (prefsOpt.isEmpty()) throw new RuntimeException("Δεν βρέθηκε Προφίλ Αναγκών");
        Preferences prefs = prefsOpt.get();

        List<Property> activeProperties = propertyRepository.findAll().stream()
                .filter(p -> p.isActive() && p.isApproved())
                .collect(Collectors.toList());

        for (Property property : activeProperties) {
            int score = engine.calculateMatchScore(property, prefs);

            // Ψάχνουμε αν υπάρχει ήδη εγγραφή για αυτό το ζεύγος Χρήστη-Ακινήτου
            Match match = matchRepository.findAll().stream()
                    .filter(m -> m.getUser().getId().equals(user.getId()) && m.getProperty().getId().equals(property.getId()))
                    .findFirst()
                    .orElse(new Match());

            match.setUser(user);
            match.setProperty(property);
            match.setScore(score);
            matchRepository.save(match);
        }
    }

    // Επιστρέφει τα κορυφαία ακίνητα για τον χρήστη, ταξινομημένα φθίνουσα
    public List<Match> getRecommendationsForUser(User user) {
        return matchRepository.findAll().stream()
                .filter(m -> m.getUser().getId().equals(user.getId()) && m.getScore() > 50) // Δείχνουμε μόνο score > 50
                .sorted((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()))
                .collect(Collectors.toList());
    }
}