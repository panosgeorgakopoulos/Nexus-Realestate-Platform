package com.nexus.realestate.service;

import com.nexus.realestate.model.Property;
import com.nexus.realestate.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    // Φέρνουμε το τηλεχειριστήριο της βάσης (Repository)
    private final PropertyRepository propertyRepository;

    // 1. Δημιουργία νέας αγγελίας
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    // 2. Ανάκτηση όλων των αγγελιών
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // 3. Ανάκτηση μιας συγκεκριμένης αγγελίας με βάση το ID
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Το ακίνητο δεν βρέθηκε!"));
    }

    // 4. Ενημέρωση αγγελίας
    public Property updateProperty(Long id, Property updatedData) {
        Property existingProperty = getPropertyById(id);

        existingProperty.setTitle(updatedData.getTitle());
        existingProperty.setDescription(updatedData.getDescription());
        existingProperty.setPrice(updatedData.getPrice());
        existingProperty.setActive(updatedData.isActive());

        return propertyRepository.save(existingProperty);
    }

    // 5. Διαγραφή αγγελίας
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}