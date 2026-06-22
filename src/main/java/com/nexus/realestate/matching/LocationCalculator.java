package com.nexus.realestate.matching;

import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.model.Property;
import org.springframework.stereotype.Component;

@Component
public class LocationCalculator {
    public int calculate(Property property, Preferences prefs) {
        if (prefs.getAreaNames() == null || prefs.getAreaNames().isBlank()) return 100;

        String propArea = property.getAreaName().toLowerCase().trim();
        String[] preferredAreas = prefs.getAreaNames().toLowerCase().split(",");

        for (String area : preferredAreas) {
            if (area.trim().equals(propArea)) {
                return 100;
            }
        }
        return 0;
    }
}