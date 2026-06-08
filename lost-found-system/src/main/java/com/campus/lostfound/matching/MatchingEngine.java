package com.campus.lostfound.matching;

import com.campus.lostfound.dao.*;
import com.campus.lostfound.model.*;
import com.campus.lostfound.service.NotificationService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatchingEngine {
    private List<MatchingStrategy> strategies = new ArrayList<>();
    private LostItemDAO lostItemDAO;
    private FoundItemDAO foundItemDAO;
    private ScheduledExecutorService scheduler;

    public MatchingEngine() {
        this.lostItemDAO = new LostItemDAOImpl();
        this.foundItemDAO = new FoundItemDAOImpl();

        // Register strategies
        strategies.add(new CategoryMatchingStrategy());
        strategies.add(new ColorMatchingStrategy());
        strategies.add(new BrandMatchingStrategy());
        strategies.add(new KeywordMatchingStrategy());
        strategies.add(new LocationMatchingStrategy());
    }

    // Weightage for each strategy
    private static final Map<Class<? extends MatchingStrategy>, Double> WEIGHTS = Map.of(
        CategoryMatchingStrategy.class, 0.35,
        ColorMatchingStrategy.class, 0.20,
        BrandMatchingStrategy.class, 0.20,
        KeywordMatchingStrategy.class, 0.15,
        LocationMatchingStrategy.class, 0.10
    );

    public List<MatchResult> findMatchesForLostItem(LostItem lostItem) throws SQLException {
        List<MatchResult> matches = new ArrayList<>();
        List<FoundItem> availableFoundItems = foundItemDAO.getAllAvailableFoundItems();

        for (FoundItem foundItem : availableFoundItems) {
            double totalScore = 0.0;
            Map<String, Double> detailedScores = new HashMap<>();

            for (MatchingStrategy strategy : strategies) {
                double score = strategy.calculateSimilarity(lostItem, foundItem);
                Double weight = WEIGHTS.get(strategy.getClass());
                double weightedScore = score * (weight != null ? weight : 0.0);

                totalScore += weightedScore;
                detailedScores.put(strategy.getClass().getSimpleName(), score);
            }

            if (totalScore >= 0.6) { // 60% threshold
                MatchResult result = new MatchResult();
                result.setLostItem(lostItem);
                result.setFoundItem(foundItem);
                result.setMatchScore(totalScore);
                result.setDetailedScores(detailedScores);
                result.setConfidenceLevel(getConfidenceLevel(totalScore));
                matches.add(result);
            }
        }

        // Sort by match score (highest first)
        matches.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return matches;
    }

    public List<MatchResult> findMatchesForFoundItem(FoundItem foundItem) throws SQLException {
        List<MatchResult> matches = new ArrayList<>();
        List<LostItem> activeLostItems = lostItemDAO.getAllActiveLostItems();

        for (LostItem lostItem : activeLostItems) {
            double totalScore = 0.0;
            Map<String, Double> detailedScores = new HashMap<>();

            for (MatchingStrategy strategy : strategies) {
                double score = strategy.calculateSimilarity(lostItem, foundItem);
                Double weight = WEIGHTS.get(strategy.getClass());
                double weightedScore = score * (weight != null ? weight : 0.0);

                totalScore += weightedScore;
                detailedScores.put(strategy.getClass().getSimpleName(), score);
            }

            if (totalScore >= 0.6) { // 60% threshold
                MatchResult result = new MatchResult();
                result.setLostItem(lostItem);
                result.setFoundItem(foundItem);
                result.setMatchScore(totalScore);
                result.setDetailedScores(detailedScores);
                result.setConfidenceLevel(getConfidenceLevel(totalScore));
                matches.add(result);
            }
        }

        // Sort by match score (highest first)
        matches.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return matches;
    }

    private String getConfidenceLevel(double score) {
        if (score >= 0.85) return "Excellent";
        if (score >= 0.75) return "Good";
        if (score >= 0.60) return "Potential";
        return "Low";
    }

    // Background matching job using multithreading
    public void startBackgroundMatching() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                performBatchMatching();
            } catch (Exception e) {
                Logger.getLogger(MatchingEngine.class.getName()).log(Level.SEVERE, "Error in background matching job", e);
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public void stopBackgroundMatching() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void performBatchMatching() throws SQLException {
        List<LostItem> activeLostItems = lostItemDAO.getAllActiveLostItems();

        for (LostItem lost : activeLostItems) {
            List<MatchResult> matches = findMatchesForLostItem(lost);
            if (!matches.isEmpty()) {
                MatchResult bestMatch = matches.get(0);
                if (bestMatch.getMatchScore() >= 0.85) {
                    // Auto-match for high confidence
                    lostItemDAO.updateLostItemStatus(lost.getLostId(), LostStatus.MATCHED);
                    foundItemDAO.updateFoundItemStatus(bestMatch.getFoundItem().getFoundId(), FoundStatus.VERIFICATION);

                    // Create auto-claim
                    createAutoClaim(bestMatch);
                }
            }
        }
    }

    private void createAutoClaim(MatchResult bestMatch) {
        try {
            Claim claim = new Claim();
            claim.setClaimantId(bestMatch.getLostItem().getUserId());
            claim.setFoundItemId(bestMatch.getFoundItem().getFoundId());
            claim.setLostItemId(bestMatch.getLostItem().getLostId());
            claim.setStatus(ClaimStatus.REVIEW); // Review status for auto-generated claims
            claim.setVerificationNotes("Auto-claim generated by matching engine (Match Score: " + 
                                       Math.round(bestMatch.getMatchScore() * 100) + "% similarity)");
            claim.setCreatedAt(LocalDateTime.now());

            ClaimDAO claimDAO = new ClaimDAOImpl();
            int claimId = claimDAO.createClaim(claim);
            claim.setClaimId(claimId);

            NotificationService notificationService = new NotificationService();
            notificationService.sendNotification(
                bestMatch.getLostItem().getUserId(),
                NotificationType.CLAIM_UPDATE,
                "Auto-Claim Generated for Match",
                "Our system generated an automatic verification claim (#" + claimId + ") for your lost item '" +
                bestMatch.getLostItem().getTitle() + "' and a found item with Excellent confidence. Security will review it shortly.",
                claimId
            );
        } catch (Exception e) {
            Logger.getLogger(MatchingEngine.class.getName()).log(Level.SEVERE, "Failed to create auto-claim", e);
        }
    }
}
