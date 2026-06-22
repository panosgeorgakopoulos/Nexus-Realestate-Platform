package com.nexus.realestate.controller;

import com.nexus.realestate.model.Preferences;
import com.nexus.realestate.service.PreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final PreferencesService preferencesService;

    // GET /api/preferences (Επιστρέφει το προφίλ του τρέχοντος συνδεδεμένου χρήστη)
    @GetMapping
    public ResponseEntity<Preferences> getPreferences() {
        try {
            return ResponseEntity.ok(preferencesService.getPreferences());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST /api/preferences (Δημιουργία προφίλ)
    @PostMapping
    public ResponseEntity<Preferences> createPreferences(@RequestBody Preferences preferences) {
        Preferences saved = preferencesService.savePreferences(preferences);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // PUT /api/preferences/{id} (Ενημέρωση προφίλ)
    @PutMapping("/{id}")
    public ResponseEntity<Preferences> updatePreferences(@PathVariable Long id, @RequestBody Preferences preferences) {
        return ResponseEntity.ok(preferencesService.updatePreferences(id, preferences));
    }
}