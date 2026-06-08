package com.campus.lostfound.dao;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.ClaimStatus;
import java.sql.SQLException;
import java.util.List;

public interface ClaimDAO {
    int createClaim(Claim claim) throws SQLException;
    Claim getClaimById(int claimId) throws SQLException;
    List<Claim> getClaimsByUser(int userId) throws SQLException;
    List<Claim> getAllClaims() throws SQLException;
    boolean hasUserClaimedItem(int claimantId, int foundItemId) throws SQLException;
    boolean updateClaimStatus(int claimId, ClaimStatus status, Integer reviewerId) throws SQLException;
    boolean updateQRCode(int claimId, String qrCodePath) throws SQLException;
    boolean updateVerificationNotes(int claimId, String notes) throws SQLException;
    boolean updateClaimAdminApproval(int claimId, ClaimStatus status, Integer adminId) throws SQLException;
    List<Claim> getClaimsByStatus(ClaimStatus status) throws SQLException;
}
