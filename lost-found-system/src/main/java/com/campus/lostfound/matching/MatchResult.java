package com.campus.lostfound.matching;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.FoundItem;
import java.util.HashMap;
import java.util.Map;

public class MatchResult {
    private LostItem lostItem;
    private FoundItem foundItem;
    private double matchScore;
    private Map<String, Double> detailedScores = new HashMap<>();
    private String confidenceLevel;

    public MatchResult() {}

    public MatchResult(LostItem lostItem, FoundItem foundItem, double matchScore, Map<String, Double> detailedScores, String confidenceLevel) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.matchScore = matchScore;
        this.detailedScores = detailedScores != null ? detailedScores : new HashMap<>();
        this.confidenceLevel = confidenceLevel;
    }

    public LostItem getLostItem() {
        return lostItem;
    }

    public void setLostItem(LostItem lostItem) {
        this.lostItem = lostItem;
    }

    public FoundItem getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(FoundItem foundItem) {
        this.foundItem = foundItem;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public Map<String, Double> getDetailedScores() {
        return detailedScores;
    }

    public void setDetailedScores(Map<String, Double> detailedScores) {
        this.detailedScores = detailedScores;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }
}
