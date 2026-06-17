package com.nexus.realestate.repository;

import com.nexus.realestate.model.PropertyFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyFeaturesRepository extends JpaRepository<PropertyFeatures, Long> {
}