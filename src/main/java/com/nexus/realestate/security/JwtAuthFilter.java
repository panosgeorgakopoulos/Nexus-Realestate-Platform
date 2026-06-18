package com.nexus.realestate.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Παίρνουμε το Header "Authorization"
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. Ελέγχουμε αν υπάρχει και αν ξεκινάει με "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Κόβουμε το "Bearer " για να κρατήσουμε μόνο το token
            username = jwtUtil.extractUsername(jwt);
        }

        // 3. Αν βρήκαμε username και ο χρήστης δεν είναι ήδη συνδεδεμένος στο SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Επαληθεύουμε το token
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 5. Ενημερώνουμε το Spring Security ότι ο χρήστης ταυτοποιήθηκε
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Συνεχίζουμε στο επόμενο φίλτρο
        filterChain.doFilter(request, response);
    }
}