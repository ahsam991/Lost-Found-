package com.campus.lostfound.service;

import com.campus.lostfound.dao.FoundItemDAO;
import com.campus.lostfound.dao.FoundItemDAOImpl;
import com.campus.lostfound.exception.ValidationException;
import com.campus.lostfound.matching.MatchResult;
import com.campus.lostfound.matching.MatchingEngine;
import com.campus.lostfound.model.FoundItem;
import com.campus.lostfound.model.NotificationType;

import java.sql.SQLException;
import java.util.List;

public class FoundItemService {
    private FoundItemDAO foundItemDAO;
    private NotificationService notificationService;

    public FoundItemService() {
        this.foundItemDAO = new FoundItemDAOImpl();
        this.notificationService = new NotificationService();
    }

    public FoundItemService(FoundItemDAO foundItemDAO, NotificationService notificationService) {
        this.foundItemDAO = foundItemDAO;
        this.notificationService = notificationService;
    }

    public int reportFoundItem(FoundItem item) throws SQLException {
        // Validate input
        if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
            throw new ValidationException("Item title is required");
        }

        if (item.getCategory() == null || item.getCategory().trim().isEmpty()) {
            throw new ValidationException("Item category is required");
        }

        // Save to database
        int foundId = foundItemDAO.reportFoundItem(item);

        // Trigger auto-matching with lost items
        // We defer MatchingEngine instantiation to avoid circular references during static loading
        try {
            MatchingEngine matchingEngine = new MatchingEngine();
            List<MatchResult> matches = matchingEngine.findMatchesForFoundItem(item);

            // Notify owners of matching lost items
            for (MatchResult match : matches) {
                notificationService.sendNotification(
                    match.getLostItem().getUserId(), 
                    NotificationType.MATCH_FOUND, 
                    "Potential Match Found for Lost Item!", 
                    "An item matching your reported lost item '" + match.getLostItem().getTitle() + 
                    "' has been found with " + match.getConfidenceLevel() + " confidence score (" + 
                    Math.round(match.getMatchScore() * 100) + "% similarity). Please check the matching listings.", 
                    match.getFoundItem().getFoundId()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundId;
    }
}
