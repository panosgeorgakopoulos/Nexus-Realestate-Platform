package com.nexus.realestate.service;

import com.nexus.realestate.dto.RegisterRequest;
import com.nexus.realestate.model.User;
import com.nexus.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {
        // Έλεγχος αν το email υπάρχει ήδη
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Το email χρησιμοποιείται ήδη!");
        }

        // Δημιουργία νέου χρήστη
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // Κρυπτογράφηση!
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setVerified(false); // Θα μπορούσε να γίνει true μετά από email verification

        return userRepository.save(user);
    }
}