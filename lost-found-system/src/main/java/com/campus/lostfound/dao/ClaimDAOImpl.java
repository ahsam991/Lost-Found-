package com.campus.lostfound.dao;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.ClaimStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimDAOImpl extends BaseDAO<Claim> implements ClaimDAO {

    public ClaimDAOImpl() {
        super();
    }

    @Override
    protected Claim mapResultSetToEntity(ResultSet rs) throws SQLException {
        Claim claim = new Claim();
        claim.setClaimId(rs.getInt("claim_id"));
        claim.setClaimantId(rs.getInt("claimant_id"));
        claim.setFoundItemId(rs.getInt("found_item_id"));
        
        int lostId = rs.getInt("lost_item_id");
        if (!rs.wasNull()) {
            claim.setLostItemId(lostId);
        }
        
        // Deserialize proofs
        String proofsStr = rs.getString("proof_documents");
        List<String> proofPaths = new ArrayList<>();
        if (proofsStr != null && !proofsStr.trim().isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(proofsStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    proofPaths.add(jsonArray.getString(i));
                }
            } catch (Exception e) {
                for (String path : proofsStr.split(",")) {
                    if (!path.trim().isEmpty()) {
                        proofPaths.add(path.trim());
                    }
                }
            }
        }
        claim.setProofDocumentPaths(proofPaths);
        
        // Deserialize security answers
        String answersStr = rs.getString("security_answers");
        Map<String, String> answers = new HashMap<>();
        if (answersStr != null && !answersStr.trim().isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(answersStr);
                for (String key : jsonObject.keySet()) {
                    answers.put(key, jsonObject.optString(key, ""));
                }
            } catch (Exception e) {
                // Keep empty map if fail
            }
        }
        claim.setSecurityAnswers(answers);
        
        claim.setQrCodePath(rs.getString("qr_code_path"));
        claim.setStatus(ClaimStatus.fromString(rs.getString("status")));
        claim.setRejectionReason(rs.getString("rejection_reason"));
        claim.setVerificationNotes(rs.getString("verification_notes"));
        
        int reviewer = rs.getInt("reviewed_by");
        if (!rs.wasNull()) {
            claim.setReviewedBy(reviewer);
        }
        
        Timestamp reviewedAtTs = rs.getTimestamp("reviewed_at");
        if (reviewedAtTs != null) {
            claim.setReviewedAt(reviewedAtTs.toLocalDateTime());
        }
        
        int adminApp = rs.getInt("admin_approved_by");
        if (!rs.wasNull()) {
            claim.setAdminApprovedBy(adminApp);
        }
        
        Timestamp adminAppTs = rs.getTimestamp("admin_approved_at");
        if (adminAppTs != null) {
            claim.setAdminApprovedAt(adminAppTs.toLocalDateTime());
        }
        
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            claim.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        return claim;
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM claims";
    }

    @Override
    public int createClaim(Claim claim) throws SQLException {
        String query = "INSERT INTO claims (claimant_id, found_item_id, lost_item_id, proof_documents, security_answers, " +
                       "qr_code_path, status, rejection_reason, verification_notes, reviewed_by, reviewed_at, " +
                       "admin_approved_by, admin_approved_at, created_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, claim.getClaimantId());
            stmt.setInt(2, claim.getFoundItemId());
            
            if (claim.getLostItemId() != null) {
                stmt.setInt(3, claim.getLostItemId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            JSONArray proofsArray = new JSONArray(claim.getProofDocumentPaths());
            stmt.setString(4, proofsArray.toString());
            
            JSONObject answersObj = new JSONObject(claim.getSecurityAnswers());
            stmt.setString(5, answersObj.toString());
            
            stmt.setString(6, claim.getQrCodePath());
            stmt.setString(7, claim.getStatus().name().toLowerCase());
            stmt.setString(8, claim.getRejectionReason());
            stmt.setString(9, claim.getVerificationNotes());
            
            if (claim.getReviewedBy() != null) {
                stmt.setInt(10, claim.getReviewedBy());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }
            
            if (claim.getReviewedAt() != null) {
                stmt.setTimestamp(11, Timestamp.valueOf(claim.getReviewedAt()));
            } else {
                stmt.setNull(11, Types.TIMESTAMP);
            }
            
            if (claim.getAdminApprovedBy() != null) {
                stmt.setInt(12, claim.getAdminApprovedBy());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }
            
            if (claim.getAdminApprovedAt() != null) {
                stmt.setTimestamp(13, Timestamp.valueOf(claim.getAdminApprovedAt()));
            } else {
                stmt.setNull(13, Types.TIMESTAMP);
            }

            stmt.setTimestamp(14, Timestamp.valueOf(claim.getCreatedAt() != null ? claim.getCreatedAt() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        claim.setClaimId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Claim getClaimById(int claimId) throws SQLException {
        String query = "SELECT * FROM claims WHERE claim_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, claimId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Claim> getClaimsByUser(int userId) throws SQLException {
        List<Claim> items = new ArrayList<>();
        String query = "SELECT * FROM claims WHERE claimant_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToEntity(rs));
                }
            }
        }
        return items;
    }

    @Override
    public List<Claim> getAllClaims() throws SQLException {
        return findAll();
    }

    @Override
    public boolean hasUserClaimedItem(int claimantId, int foundItemId) throws SQLException {
        String query = "SELECT COUNT(*) FROM claims WHERE claimant_id = ? AND found_item_id = ? AND status != 'rejected'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, claimantId);
            stmt.setInt(2, foundItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateClaimStatus(int claimId, ClaimStatus status, Integer reviewerId) throws SQLException {
        String query = "UPDATE claims SET status = ?, reviewed_by = ?, reviewed_at = ? WHERE claim_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.name().toLowerCase());
            if (reviewerId != null) {
                stmt.setInt(2, reviewerId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, claimId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateQRCode(int claimId, String qrCodePath) throws SQLException {
        String query = "UPDATE claims SET qr_code_path = ? WHERE claim_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, qrCodePath);
            stmt.setInt(2, claimId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateVerificationNotes(int claimId, String notes) throws SQLException {
        String query = "UPDATE claims SET verification_notes = ? WHERE claim_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, notes);
            stmt.setInt(2, claimId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateClaimAdminApproval(int claimId, ClaimStatus status, Integer adminId) throws SQLException {
        String query = "UPDATE claims SET status = ?, admin_approved_by = ?, admin_approved_at = ? WHERE claim_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.name().toLowerCase());
            if (adminId != null) {
                stmt.setInt(2, adminId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, claimId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Claim> getClaimsByStatus(ClaimStatus status) throws SQLException {
        List<Claim> items = new ArrayList<>();
        String query = "SELECT * FROM claims WHERE status = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.name().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToEntity(rs));
                }
            }
        }
        return items;
    }
}
