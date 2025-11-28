package com.shashwath.smartjobrecommender.controller;

import com.shashwath.smartjobrecommender.service.TfIdfService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendController {

    private final TfIdfService tfIdfService;

    public RecommendController(TfIdfService tfIdfService) {
        this.tfIdfService = tfIdfService;
    }

    @GetMapping("/recommend")
    public List<TfIdfService.ScoredJob> recommend(@RequestParam String query, @RequestParam(defaultValue = "5") int limit) {
        return tfIdfService.recommend(query, limit);
    }
}
