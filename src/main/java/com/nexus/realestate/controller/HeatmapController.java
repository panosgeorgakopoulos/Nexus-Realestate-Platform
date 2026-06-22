package com.nexus.realestate.controller;

import com.nexus.realestate.repository.ViewHistoryRepository;
import com.nexus.realestate.model.ViewHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heatmap")
@RequiredArgsConstructor
public class HeatmapController {

    private final ViewHistoryRepository viewHistoryRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getHeatmapData() {
        // Παίρνουμε όλες τις προβολές
        List<ViewHistory> views = viewHistoryRepository.findAll();

        // Ομαδοποιούμε τις προβολές ανά ακίνητο (lat, lng) και μετράμε την "ένταση" (intensity)
        Map<Long, Map<String, Object>> heatmapMap = new HashMap<>();

        for (ViewHistory view : views) {
            Long propId = view.getProperty().getId();
            BigDecimal lat = view.getProperty().getLat();
            BigDecimal lng = view.getProperty().getLng();

            if (lat != null && lng != null) {
                if (!heatmapMap.containsKey(propId)) {
                    Map<String, Object> pointData = new HashMap<>();
                    pointData.put("lat", lat);
                    pointData.put("lng", lng);
                    pointData.put("intensity", 1);
                    heatmapMap.put(propId, pointData);
                } else {
                    Map<String, Object> existingData = heatmapMap.get(propId);
                    existingData.put("intensity", (Integer) existingData.get("intensity") + 1);
                }
            }
        }

        return ResponseEntity.ok(heatmapMap.values().stream().toList());
    }
}