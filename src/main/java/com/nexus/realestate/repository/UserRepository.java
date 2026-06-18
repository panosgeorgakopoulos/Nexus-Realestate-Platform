package com.nexus.realestate.repository;

import com.nexus.realestate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Αναζήτηση χρήστη με βάση το email
    Optional<User> findByEmail(String email);
}