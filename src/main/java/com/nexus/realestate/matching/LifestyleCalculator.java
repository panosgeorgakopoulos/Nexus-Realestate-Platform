package com.nexus.realestate.matching;

import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.model.Property;
import org.springframework.stereotype.Component;

@Component
public class LifestyleCalculator {
    public int calculate(Property property, Preferences prefs) {
        int score = 100;

        // 1. Βασικός έλεγχος δωματίων
        if (prefs.getRoomsMin() != null && property.getRooms() != null) {
            if (property.getRooms() < prefs.getRoomsMin()) score -= 30;
        }

        // 2. Εξειδικευμένα Lifestyle rules
        if (prefs.getLifestyleType() != null) {
            switch (prefs.getLifestyleType()) {
                case FAMILY:
                    if (property.getFloor() != null && property.getFloor() > 1) score += 10; // Οικογένειες συνήθως προτιμούν ορόφους
                    break;
                case STUDENT:
                    if (property.getSqm().doubleValue() < 60) score += 10; // Φοιτητές προτιμούν μικρότερα
                    break;
                case RETIRED:
                    if (property.getFloor() != null && property.getFloor() > 1) score -= 20; // Χωρίς ασανσέρ (που το ελέγχουμε αλλού), ο όροφος είναι αρνητικός
                    break;
            }
        }

        return Math.max(Math.min(score, 100), 0); // Κρατάμε το score αυστηρά μεταξύ 0 και 100
    }
}