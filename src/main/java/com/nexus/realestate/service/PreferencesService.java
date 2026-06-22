package com.nexus.realestate.service;

import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.model.User;
import com.nexus.realestate.repository.PreferencesRepository;
import com.nexus.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    // Βοηθητική μέθοδος για την ανάκτηση του τρέχοντος συνδεδεμένου χρήστη
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Ο συνδεδεμένος χρήστης δεν βρέθηκε στη βάση."));
    }

    public Preferences getPreferences() {
        User user = getAuthenticatedUser();
        return preferencesRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Δεν έχει δημιουργηθεί προφίλ προτιμήσεων για αυτόν τον χρήστη."));
    }

    public Preferences savePreferences(Preferences preferences) {
        User user = getAuthenticatedUser();
        preferences.setUser(user);
        return preferencesRepository.save(preferences);
    }

    public Preferences updatePreferences(Long id, Preferences updatedData) {
        Preferences existing = preferencesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Το προφίλ προτιμήσεων δεν βρέθηκε."));

        existing.setBudgetMin(updatedData.getBudgetMin());
        existing.setBudgetMax(updatedData.getBudgetMax());
        existing.setAreaNames(updatedData.getAreaNames());
        existing.setPropertyType(updatedData.getPropertyType());
        existing.setSqmMin(updatedData.getSqmMin());
        existing.setRoomsMin(updatedData.getRoomsMin());
        existing.setLifestyleType(updatedData.getLifestyleType());
        existing.setNeedsParking(updatedData.isNeedsParking());
        existing.setNeedsElevator(updatedData.isNeedsElevator());
        existing.setListingStatus(updatedData.getListingStatus());

        return preferencesRepository.save(existing);
    }
}