package com.nexus.realestate.config;

import com.nexus.realestate.enums.*;
import com.nexus.realestate.model.*;
import com.nexus.realestate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyFeaturesRepository featuresRepository;
    private final MatchWeightRepository matchWeightRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Αν η βάση έχει ήδη χρήστες, δεν τρέχουμε το Seeder ξανά
        if (userRepository.count() > 0) {
            System.out.println("Η βάση έχει ήδη δεδομένα. Το Seeder παραλείπεται.");
            return;
        }

        System.out.println("Εκκίνηση Data Seeder: Δημιουργία ρεαλιστικών δεδομένων...");

        // 1. Δημιουργία Χρηστών
        List<User> owners = new ArrayList<>();
        List<User> buyers = new ArrayList<>();

        // Κοινός κωδικός '123456' για όλους για εύκολο login στην παρουσίαση
        String commonPassword = passwordEncoder.encode("123456");

        // --- ADMIN ---
        User admin = new User();
        admin.setEmail("admin@nexus.gr");
        admin.setPasswordHash(commonPassword);
        admin.setFullName("Super Admin");
        admin.setRole(UserRole.ADMIN);
        admin.setVerified(true);
        userRepository.save(admin);

        // --- OWNERS (3 Ιδιοκτήτες/Μεσίτες) ---
        for (int i = 1; i <= 3; i++) {
            User owner = new User();
            owner.setEmail("owner" + i + "@nexus.gr");
            owner.setPasswordHash(commonPassword);
            owner.setFullName("Ιδιοκτήτης " + i);
            owner.setPhone("697000000" + i);
            owner.setRole(UserRole.OWNER);
            owner.setVerified(true);
            owners.add(userRepository.save(owner));
        }

        // --- BUYERS (10 Αγοραστές/Ενοικιαστές) ---
        for (int i = 1; i <= 10; i++) {
            User buyer = new User();
            buyer.setEmail("buyer" + i + "@nexus.gr");
            buyer.setPasswordHash(commonPassword);
            buyer.setFullName("Αγοραστής " + i);
            buyer.setRole(UserRole.BUYER);
            buyer.setVerified(true);
            buyers.add(userRepository.save(buyer));
        }

        // 2. Δημιουργία Βαρών Αλγορίθμου (Αν δεν υπάρχουν)
        if (matchWeightRepository.count() == 0) {
            saveWeight("budget", 35.0);
            saveWeight("location", 30.0);
            saveWeight("size", 15.0);
            saveWeight("lifestyle", 20.0);
        }

        // 3. Δεδομένα Πραγματικών Περιοχών με Συντεταγμένες (Κέντρο Περιοχής)
        String[] areas = {"Κολωνάκι", "Γλυφάδα", "Κηφισιά", "Παγκράτι", "Περιστέρι", "Χαλάνδρι", "Πειραιάς", "Καλαμαριά", "Κέντρο Θεσσαλονίκης"};
        double[][] coords = {
                {37.9775, 23.7420}, {37.8628, 23.7558}, {38.0735, 23.8116}, // Κολωνάκι, Γλυφάδα, Κηφισιά
                {37.9680, 23.7420}, {38.0120, 23.6890}, {38.0220, 23.7990}, // Παγκράτι, Περιστέρι, Χαλάνδρι
                {37.9430, 23.6469}, {40.5828, 22.9510}, {40.6324, 22.9432}  // Πειραιάς, Καλαμαριά, Κέντρο Θεσσαλονίκης
        };

        String[] adjectives = {"Πολυτελές", "Μοντέρνο", "Ανακαινισμένο", "Ευρύχωρο", "Κλασικό", "Φωτεινό", "Επενδυτικό"};

        Random rand = new Random();

        // 4. Δημιουργία 45 Ρεαλιστικών Ακινήτων
        for (int i = 1; i <= 45; i++) {
            int areaIdx = rand.nextInt(areas.length);
            String area = areas[areaIdx];

            PropertyType type = rand.nextDouble() > 0.3 ? PropertyType.APARTMENT : PropertyType.HOUSE; // 70% διαμερίσματα
            ListingStatus status = rand.nextBoolean() ? ListingStatus.FOR_SALE : ListingStatus.FOR_RENT;

            int sqm = type == PropertyType.APARTMENT ? (rand.nextInt(100) + 50) : (rand.nextInt(200) + 100);

            // Ρεαλιστικός Υπολογισμός Τιμής
            double basePricePerSqmSale = 2500 + rand.nextInt(2500); // 2500 - 5000 €/τ.μ. για αγορά
            double basePricePerSqmRent = 8 + rand.nextInt(10);      // 8 - 18 €/τ.μ. για ενοίκιο

            BigDecimal price = status == ListingStatus.FOR_SALE
                    ? BigDecimal.valueOf(sqm * basePricePerSqmSale)
                    : BigDecimal.valueOf(sqm * basePricePerSqmRent);

            Property p = new Property();
            p.setOwner(owners.get(rand.nextInt(owners.size()))); // Τυχαίος ιδιοκτήτης
            p.setTitle(adjectives[rand.nextInt(adjectives.length)] + (type == PropertyType.APARTMENT ? " Διαμέρισμα" : " Μονοκατοικία") + " στην περιοχή " + area);
            p.setDescription("Ένα εξαιρετικό ακίνητο σε προνομιακή τοποθεσία. Προσφέρει άνεση και ποιότητα κατασκευής. Ιδανικό για οικογένειες ή επαγγελματίες.");
            p.setType(type);
            p.setStatus(status);
            p.setPrice(price.setScale(2, RoundingMode.HALF_UP));
            p.setSqm(BigDecimal.valueOf(sqm).setScale(2, RoundingMode.HALF_UP));
            p.setRooms(sqm < 80 ? 1 : (sqm < 120 ? 2 : 3 + rand.nextInt(2)));
            p.setBathrooms(sqm < 100 ? 1 : 2);
            p.setFloor(type == PropertyType.HOUSE ? 0 : rand.nextInt(6) + 1);
            p.setYearBuilt(1980 + rand.nextInt(44)); // 1980 - 2024
            p.setAreaName(area);
            p.setAddress("Τυχαία Οδός " + (rand.nextInt(100) + 1) + ", " + area);

            // Προσθήκη μικρού "θορύβου" (+/- 0.01) στις συντεταγμένες για να μην πέφτουν τα markers στο ίδιο ακριβώς pixel στον χάρτη
            double latJitter = (rand.nextDouble() - 0.5) * 0.02;
            double lngJitter = (rand.nextDouble() - 0.5) * 0.02;
            p.setLat(BigDecimal.valueOf(coords[areaIdx][0] + latJitter));
            p.setLng(BigDecimal.valueOf(coords[areaIdx][1] + lngJitter));

            p.setApproved(true); // Εγκεκριμένα για να φαίνονται στο demo
            p.setActive(true);

            Property savedProperty = propertyRepository.save(p);

            // 5. Δημιουργία Χαρακτηριστικών για κάθε ακίνητο
            PropertyFeatures features = new PropertyFeatures();
            features.setProperty(savedProperty);
            features.setHasElevator(p.getFloor() > 1 || rand.nextBoolean());
            features.setHasParking(rand.nextBoolean());
            features.setHasBalcony(true);
            features.setHasStorage(rand.nextBoolean());
            features.setFurnished(status == ListingStatus.FOR_RENT && rand.nextBoolean());
            features.setPetsAllowed(rand.nextBoolean());
            features.setHeatingType(HeatingType.values()[rand.nextInt(HeatingType.values().length - 1)]); // Αποφυγή NONE
            features.setEnergyClass(EnergyClass.values()[rand.nextInt(5)]); // Α, Β, Γ κτλ.

            featuresRepository.save(features);
        }

        System.out.println("✅ Το Seeder ολοκληρώθηκε! Προστέθηκαν " + owners.size() + " Ιδιοκτήτες, " + buyers.size() + " Αγοραστές και 45 Ακίνητα.");
    }

    private void saveWeight(String criterion, double weight) {
        MatchWeight mw = new MatchWeight();
        mw.setCriterion(criterion);
        mw.setWeight(BigDecimal.valueOf(weight));
        matchWeightRepository.save(mw);
    }
}