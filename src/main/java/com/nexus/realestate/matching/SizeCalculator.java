package com.nexus.realestate.matching;

import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.model.Property;
import org.springframework.stereotype.Component;

@Component
public class SizeCalculator {
    public int calculate(Property property, Preferences prefs) {
        if (prefs.getSqmMin() == null) return 100;

        double propSqm = property.getSqm().doubleValue();
        double minSqm = prefs.getSqmMin().doubleValue();

        if (propSqm >= minSqm) return 100;

        // Αναλογική μείωση: χάνει 10 πόντους για κάθε 10% που υπολείπεται
        double ratio = propSqm / minSqm;
        int score = (int) (ratio * 100);
        return Math.max(score, 0);
    }
}