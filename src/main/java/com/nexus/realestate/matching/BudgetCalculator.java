package com.nexus.realestate.matching;

import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.model.Property;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BudgetCalculator {
    public int calculate(Property property, Preferences prefs) {
        if (prefs.getBudgetMax() == null) return 100; // Αν δεν έχει όριο, ταιριάζει τέλεια

        double price = property.getPrice().doubleValue();
        double maxBudget = prefs.getBudgetMax().doubleValue();

        if (price <= maxBudget) {
            return 100; // Μέσα στο budget
        } else if (price <= maxBudget * 1.10) {
            return 60;  // Μέχρι 10% πάνω από το budget
        }
        return 0; // Απαγορευτική τιμή
    }
}