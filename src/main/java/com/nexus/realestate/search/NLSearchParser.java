package com.nexus.realestate.search;

import com.nexus.realestate.dto.SearchCriteria;
import com.nexus.realestate.enums.ListingStatus;
import com.nexus.realestate.enums.PropertyType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NLSearchParser {

    public SearchCriteria parse(String query) {
        SearchCriteria criteria = new SearchCriteria();
        if (query == null || query.isBlank()) {
            return criteria;
        }

        String lowerQuery = query.toLowerCase();

        // 1. Υπνοδωμάτια
        Matcher roomMatcher = Pattern.compile("(\\d+)\\s*(υπνοδωμάτια|δωμάτια|bedrooms)").matcher(lowerQuery);
        if (roomMatcher.find()) {
            criteria.setRoomsMin(Integer.parseInt(roomMatcher.group(1)));
        }

        // 2. Budget (έως / μέχρι)
        Matcher budgetMaxMatcher = Pattern.compile("(έως|μέχρι|max)\\s*(\\d+)").matcher(lowerQuery);
        if (budgetMaxMatcher.find()) {
            criteria.setBudgetMax(new BigDecimal(budgetMaxMatcher.group(2)));
        }

        // 3. Budget (από / min)
        Matcher budgetMinMatcher = Pattern.compile("(από|min)\\s*(\\d+)\\s*(ευρώ|euro|€)").matcher(lowerQuery);
        if (budgetMinMatcher.find()) {
            criteria.setBudgetMin(new BigDecimal(budgetMinMatcher.group(2)));
        }

        // Αν γράψει σκέτο π.χ. "800 ευρώ", το θεωρούμε μέγιστο budget
        Matcher exactBudgetMatcher = Pattern.compile("(\\d+)\\s*(ευρώ|euro|€)").matcher(lowerQuery);
        if (exactBudgetMatcher.find() && criteria.getBudgetMax() == null) {
            criteria.setBudgetMax(new BigDecimal(exactBudgetMatcher.group(1)));
        }

        // 4. Τετραγωνικά
        Matcher sqmMatcher = Pattern.compile("(\\d+)\\s*(τ\\.μ\\.|τετραγωνικά|sqm)").matcher(lowerQuery);
        if (sqmMatcher.find()) {
            criteria.setSqmMin(new BigDecimal(sqmMatcher.group(1)));
        }

        // 5. Λέξεις-κλειδιά (Booleans)
        if (lowerQuery.contains("πάρκινγκ") || lowerQuery.contains("parking")) {
            // criteria.setNeedsParking(true); // Αν έχεις προσθέσει το πεδίο στο SearchCriteria
        }

        // 6. Τύπος Ακινήτου & Κατάσταση
        if (lowerQuery.contains("διαμέρισμα") || lowerQuery.contains("apartment")) {
            criteria.setPropertyType(PropertyType.APARTMENT);
        } else if (lowerQuery.contains("μονοκατοικία") || lowerQuery.contains("house")) {
            criteria.setPropertyType(PropertyType.HOUSE);
        }

        if (lowerQuery.contains("ενοίκιο") || lowerQuery.contains("ενοικίαση") || lowerQuery.contains("rent")) {
            criteria.setListingStatus(ListingStatus.FOR_RENT);
        } else if (lowerQuery.contains("αγορά") || lowerQuery.contains("πώληση") || lowerQuery.contains("sale")) {
            criteria.setListingStatus(ListingStatus.FOR_SALE);
        }
        // 7. Λέξεις-κλειδιά: Περιοχές
        String[] knownAreas = {"κολωνάκι", "γλυφάδα", "κηφισιά", "παγκράτι", "περιστέρι", "χαλάνδρι", "πειραιάς", "καλαμαριά", "θεσσαλονίκη"};
        for (String area : knownAreas) {
            if (lowerQuery.contains(area)) {
                // Κάνει capitalize το πρώτο γράμμα για να κάνει match με τη βάση
                String formattedArea = area.substring(0, 1).toUpperCase() + area.substring(1);
                criteria.setAreaName(formattedArea);
                break;
            }
        }
        return criteria;
    }
}