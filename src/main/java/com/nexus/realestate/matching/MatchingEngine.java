package com.nexus.realestate.matching;

import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.model.Property;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MatchingEngine {

    private final BudgetCalculator budgetCalc;
    private final LocationCalculator locationCalc;
    private final SizeCalculator sizeCalc;
    private final LifestyleCalculator lifestyleCalc;
    private final WeightConfigLoader weightLoader;

    public int calculateMatchScore(Property property, Preferences prefs) {
        Map<String, Double> weights = weightLoader.loadWeights();

        double bScore = budgetCalc.calculate(property, prefs) * (weights.getOrDefault("budget", 35.0) / 100);
        double lScore = locationCalc.calculate(property, prefs) * (weights.getOrDefault("location", 30.0) / 100);
        double sScore = sizeCalc.calculate(property, prefs) * (weights.getOrDefault("size", 15.0) / 100);
        double lifeScore = lifestyleCalc.calculate(property, prefs) * (weights.getOrDefault("lifestyle", 20.0) / 100);

        int finalScore = (int) Math.round(bScore + lScore + sScore + lifeScore);
        return Math.min(finalScore, 100);
    }
}