package com.nexus.realestate.repository;

import com.nexus.realestate.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    // Αναζήτηση όλων των αγγελιών που ΔΕΝ έχουν εγκριθεί ακόμα
    List<Property> findByApprovedFalse();
}
