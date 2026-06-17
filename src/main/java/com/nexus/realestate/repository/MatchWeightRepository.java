package com.nexus.realestate.repository;

import com.nexus.realestate.model.MatchWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchWeightRepository extends JpaRepository<MatchWeight, Long> {
}
