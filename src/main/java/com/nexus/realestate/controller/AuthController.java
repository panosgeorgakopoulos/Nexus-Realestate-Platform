package com.nexus.realestate.controller;

import com.nexus.realestate.dto.LoginRequest;
import com.nexus.realestate.dto.RegisterRequest;
import com.nexus.realestate.security.JwtUtil;
import com.nexus.realestate.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    // Endpoint για Εγγραφή
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.registerUser(request);
            return ResponseEntity.ok("Η εγγραφή ολοκληρώθηκε με επιτυχία!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint για Σύνδεση (Login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Επαλήθευση κωδικού μέσω Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            // Πλέον αν ο κωδικός είναι λάθος δεν βγάζει 404, αλλά σωστό μήνυμα λάθους!
            Map<String, String> error = new HashMap<>();
            error.put("message", "Λάθος email ή κωδικός!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Αν είναι σωστά τα στοιχεία, φορτώνουμε τον χρήστη και δημιουργούμε το Token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails); // Του περνάμε όλο το userDetails για να συμπεριλάβει και τα Roles στο JWT

        // Επιστρέφουμε το Token σε μορφή JSON
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }
}