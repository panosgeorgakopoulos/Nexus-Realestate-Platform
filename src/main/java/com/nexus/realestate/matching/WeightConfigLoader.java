package com.nexus.realestate.matching;

import com.nexus.realestate.model.MatchWeight;
import com.nexus.realestate.repository.MatchWeightRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeightConfigLoader {

    private final MatchWeightRepository repository;

    @PostConstruct
    public void initDefaultWeights() {
        if (repository.count() == 0) {
            saveWeight("budget", 35.0);
            saveWeight("location", 30.0);
            saveWeight("lifestyle", 20.0);
            saveWeight("size", 15.0);
        }
    }

    private void saveWeight(String criterion, double weight) {
        MatchWeight mw = new MatchWeight();
        mw.setCriterion(criterion);
        mw.setWeight(BigDecimal.valueOf(weight));
        repository.save(mw);
    }

    public Map<String, Double> loadWeights() {
        List<MatchWeight> weights = repository.findAll();
        Map<String, Double> map = new HashMap<>();
        for (MatchWeight w : weights) {
            map.put(w.getCriterion(), w.getWeight().doubleValue());
        }
        return map;
    }
}