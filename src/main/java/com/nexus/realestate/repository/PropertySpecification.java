package com.nexus.realestate.repository;

import com.nexus.realestate.dto.SearchCriteria;
import com.nexus.realestate.model.Property;
import com.nexus.realestate.enums.PropertyType;
import com.nexus.realestate.enums.ListingStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PropertySpecification {

    public static Specification<Property> filterProperties(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getBudgetMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.getBudgetMin()));
            }
            if (criteria.getBudgetMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.getBudgetMax()));
            }
            if (criteria.getAreaName() != null && !criteria.getAreaName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("areaName")),
                        "%" + criteria.getAreaName().toLowerCase() + "%"
                ));
            }
            if (criteria.getPropertyType() != null && criteria.getPropertyType() != PropertyType.ANY) {
                predicates.add(criteriaBuilder.equal(root.get("type"), criteria.getPropertyType()));
            }
            if (criteria.getSqmMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sqm"), criteria.getSqmMin()));
            }
            if (criteria.getRoomsMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rooms"), criteria.getRoomsMin()));
            }
            if (criteria.getListingStatus() != null && criteria.getListingStatus() != ListingStatus.ANY) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getListingStatus()));
            }

            // Εξασφαλίζουμε ότι στην αναζήτηση εμφανίζονται μόνο ενεργές και εγκεκριμένες αγγελίες
            predicates.add(criteriaBuilder.equal(root.get("approved"), true));
            predicates.add(criteriaBuilder.equal(root.get("active"), true));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}