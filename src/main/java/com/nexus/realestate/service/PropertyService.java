package com.nexus.realestate.service;

import com.nexus.realestate.dto.SearchCriteria;
import com.nexus.realestate.model.PriceHistory;
import com.nexus.realestate.model.Property;
import com.nexus.realestate.repository.PriceHistoryRepository;
import com.nexus.realestate.repository.PropertyRepository;
import com.nexus.realestate.repository.PropertySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Το ακίνητο δεν βρέθηκε!"));
    }

    @Transactional
    public Property updateProperty(Long id, Property updatedData) {
        Property existingProperty = getPropertyById(id);

        // Έλεγχος Αλλαγής Τιμής -> Αυτόματο Price History Pattern
        if (updatedData.getPrice() != null && !existingProperty.getPrice().equals(updatedData.getPrice())) {
            PriceHistory history = new PriceHistory();
            history.setProperty(existingProperty);
            history.setOldPrice(existingProperty.getPrice());
            history.setNewPrice(updatedData.getPrice());
            history.setChangeReason("Ενημέρωση τιμής από τον ιδιοκτήτη");
            priceHistoryRepository.save(history);
        }

        existingProperty.setTitle(updatedData.getTitle());
        existingProperty.setDescription(updatedData.getDescription());
        existingProperty.setPrice(updatedData.getPrice());
        existingProperty.setActive(updatedData.isActive());

        return propertyRepository.save(existingProperty);
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    // Δυναμική αναζήτηση με specifications
    public List<Property> searchProperties(SearchCriteria criteria) {
        return propertyRepository.findAll(PropertySpecification.filterProperties(criteria));
    }
}