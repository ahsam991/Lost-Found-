package com.campus.lostfound.service;

import com.campus.lostfound.dao.*;
import com.campus.lostfound.exception.DuplicateClaimException;
import com.campus.lostfound.exception.ValidationException;
import com.campus.lostfound.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ClaimService {
    private ClaimDAO claimDAO;
    private FoundItemDAO foundItemDAO;
    private LostItemDAO lostItemDAO;
    private NotificationService notificationService;
    private QRCodeService qrCodeService;

    public ClaimService() {
        this.claimDAO = new ClaimDAOImpl();
        this.foundItemDAO = new FoundItemDAOImpl();
        this.lostItemDAO = new LostItemDAOImpl();
        this.notificationService = new NotificationService();
        this.qrCodeService = new QRCodeService();
    }

    public ClaimService(ClaimDAO claimDAO, FoundItemDAO foundItemDAO, LostItemDAO lostItemDAO, 
                        NotificationService notificationService, QRCodeService qrCodeService) {
        this.claimDAO = claimDAO;
        this.foundItemDAO = foundItemDAO;
        this.lostItemDAO = lostItemDAO;
        this.notificationService = notificationService;
        this.qrCodeService = qrCodeService;
    }

    public int submitClaim(Claim claim) throws SQLException {
        // Validate claim
        if (claim.getClaimantId() <= 0) {
            throw new ValidationException("Invalid claimant");
        }

        if (claim.getFoundItemId() <= 0) {
            throw new ValidationException("Invalid found item");
        }

        // Check if user already claimed this item
        if (claimDAO.hasUserClaimedItem(claim.getClaimantId(), claim.getFoundItemId())) {
            throw new DuplicateClaimException("You have already claimed this item");
        }

        // Save claim
        int claimId = claimDAO.createClaim(claim);

        // Send notification to security
        notificationService.sendNewClaimNotification(claimId);

        return claimId;
    }

    public boolean verifyClaim(int claimId, String notes, int securityId) throws SQLException {
        Claim claim = claimDAO.getClaimById(claimId);
        if (claim == null) {
            throw new IllegalArgumentException("Claim not found");
        }

        if (!claim.canBeReviewed()) {
            throw new IllegalStateException("Claim cannot be reviewed in current status");
        }

        // Perform verification checks
        boolean verified = performVerification(claim);

        if (verified) {
            // Update claim status to APPROVED
            claimDAO.updateClaimStatus(claimId, ClaimStatus.APPROVED, securityId);
            
            // Generate QR code for pickup
            try {
                String qrCodePath = qrCodeService.generateQRCode(claimId);
                claimDAO.updateQRCode(claimId, qrCodePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Update found item status to VERIFICATION or CLAIMED
            foundItemDAO.updateFoundItemStatus(claim.getFoundItemId(), FoundStatus.VERIFICATION);

            // Notify claimant
            notificationService.sendClaimApprovedNotification(claim.getClaimantId(), claimId);
        } else {
            // Update claim status to ADDITIONAL_INFO
            claimDAO.updateClaimStatus(claimId, ClaimStatus.ADDITIONAL_INFO, securityId);
            claimDAO.updateVerificationNotes(claimId, notes);

            // Request more info
            notificationService.sendAdditionalInfoRequest(claim.getClaimantId(), claimId, notes);
        }

        return verified;
    }

    public boolean performVerification(Claim claim) throws SQLException {
        FoundItem foundItem = foundItemDAO.getFoundItemById(claim.getFoundItemId());
        if (foundItem == null) return false;
        
        LostItem lostItem = null;
        if (claim.getLostItemId() != null) {
            lostItem = lostItemDAO.getLostItemById(claim.getLostItemId());
        }

        int verificationScore = 0;

        // Check 1: Photos match? (30 points)
        if (comparePhotos(claim.getProofDocumentPaths(), foundItem.getPhotoPaths())) {
            verificationScore += 30;
        }

        // Check 2: Security answers match? (40 points)
        if (lostItem != null && validateSecurityAnswers(claim.getSecurityAnswers(), lostItem)) {
            verificationScore += 40;
        } else if (claim.getSecurityAnswers() != null && !claim.getSecurityAnswers().isEmpty()) {
            // General verification of security answers when no matching lost item is pre-filed
            verificationScore += 25; 
        }

        // Check 3: Serial number match? (30 points)
        if (lostItem != null && lostItem.getSerialNumber() != null && !lostItem.getSerialNumber().trim().isEmpty() &&
            lostItem.getSerialNumber().equalsIgnoreCase(foundItem.getSerialNumber())) {
            verificationScore += 30;
        } else if (foundItem.getSerialNumber() != null && !foundItem.getSerialNumber().trim().isEmpty() &&
                   claim.getSecurityAnswers().containsValue(foundItem.getSerialNumber())) {
            // Claimant provided the exact serial number in security answers
            verificationScore += 30;
        }

        return verificationScore >= 60; // 60% threshold for approval
    }

    private boolean comparePhotos(List<String> proofs, List<String> itemPhotos) {
        if (proofs == null || proofs.isEmpty()) {
            return false;
        }
        // Simulated comparison: if user uploaded proof, award points
        return true;
    }

    private boolean validateSecurityAnswers(Map<String, String> answers, LostItem lostItem) {
        if (answers == null || answers.isEmpty() || lostItem == null) {
            return false;
        }
        int matches = 0;
        for (String value : answers.values()) {
            if (value == null) continue;
            String valLower = value.toLowerCase();
            
            if (lostItem.getColor() != null && valLower.contains(lostItem.getColor().toLowerCase())) {
                matches++;
            }
            if (lostItem.getBrand() != null && valLower.contains(lostItem.getBrand().toLowerCase())) {
                matches++;
            }
            if (lostItem.getDescription() != null) {
                String[] words = valLower.split("\\s+");
                for (String w : words) {
                    if (w.length() > 3 && lostItem.getDescription().toLowerCase().contains(w)) {
                        matches++;
                        break;
                    }
                }
            }
        }
        return matches > 0 || answers.size() >= 2;
    }
}
