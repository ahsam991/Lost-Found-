package com.campus.lostfound.dao;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.LostStatus;
import org.json.JSONArray;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LostItemDAOImpl extends BaseDAO<LostItem> implements LostItemDAO {

    public LostItemDAOImpl() {
        super();
    }

    @Override
    protected LostItem mapResultSetToEntity(ResultSet rs) throws SQLException {
        LostItem item = new LostItem();
        item.setLostId(rs.getInt("lost_id"));
        item.setUserId(rs.getInt("user_id"));
        item.setTitle(rs.getString("title"));
        item.setCategory(rs.getString("category"));
        item.setBrand(rs.getString("brand"));
        item.setModel(rs.getString("model"));
        item.setColor(rs.getString("color"));
        item.setSerialNumber(rs.getString("serial_number"));
        item.setDescription(rs.getString("description"));
        item.setLocationLost(rs.getString("location_lost"));
        item.setLocationLat(rs.getString("location_lat"));
        item.setLocationLng(rs.getString("location_lng"));
        
        Date dateLostDb = rs.getDate("date_lost");
        if (dateLostDb != null) {
            item.setDateLost(dateLostDb.toLocalDate());
        }
        
        item.setEstimatedValue(rs.getBigDecimal("estimated_value"));
        
        // Deserialize photo paths from JSON or comma-separated format
        String photosStr = rs.getString("photos");
        List<String> photoPaths = new ArrayList<>();
        if (photosStr != null && !photosStr.trim().isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(photosStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    photoPaths.add(jsonArray.getString(i));
                }
            } catch (Exception e) {
                // Fallback to comma-split
                for (String path : photosStr.split(",")) {
                    if (!path.trim().isEmpty()) {
                        photoPaths.add(path.trim());
                    }
                }
            }
        }
        item.setPhotoPaths(photoPaths);
        
        item.setStatus(LostStatus.fromString(rs.getString("status")));
        
        double score = rs.getDouble("match_score");
        if (!rs.wasNull()) {
            item.setMatchScore(score);
        }
        
        int matchedId = rs.getInt("matched_item_id");
        if (!rs.wasNull()) {
            item.setMatchedItemId(matchedId);
        }
        
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            item.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            item.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }

        return item;
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM lost_items";
    }

    @Override
    public int reportLostItem(LostItem item) throws SQLException {
        String query = "INSERT INTO lost_items (user_id, title, category, brand, model, color, serial_number, description, " +
                       "location_lost, location_lat, location_lng, date_lost, estimated_value, photos, status, match_score, matched_item_id, created_at, updated_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getUserId());
            stmt.setString(2, item.getTitle());
            stmt.setString(3, item.getCategory());
            stmt.setString(4, item.getBrand());
            stmt.setString(5, item.getModel());
            stmt.setString(6, item.getColor());
            stmt.setString(7, item.getSerialNumber());
            stmt.setString(8, item.getDescription());
            stmt.setString(9, item.getLocationLost());
            stmt.setString(10, item.getLocationLat());
            stmt.setString(11, item.getLocationLng());
            
            stmt.setDate(12, item.getDateLost() != null ? Date.valueOf(item.getDateLost()) : Date.valueOf(LocalDate.now()));
            stmt.setBigDecimal(13, item.getEstimatedValue());
            
            // Serialize photos as JSON array
            JSONArray jsonArray = new JSONArray(item.getPhotoPaths());
            stmt.setString(14, jsonArray.toString());
            
            stmt.setString(15, item.getStatus().name().toLowerCase());
            
            if (item.getMatchScore() != null) {
                stmt.setDouble(16, item.getMatchScore());
            } else {
                stmt.setNull(16, Types.DECIMAL);
            }
            
            if (item.getMatchedItemId() != null) {
                stmt.setInt(17, item.getMatchedItemId());
            } else {
                stmt.setNull(17, Types.INTEGER);
            }

            stmt.setTimestamp(18, Timestamp.valueOf(item.getCreatedAt() != null ? item.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(19, Timestamp.valueOf(item.getUpdatedAt() != null ? item.getUpdatedAt() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        item.setLostId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public LostItem getLostItemById(int lostId) throws SQLException {
        String query = "SELECT * FROM lost_items WHERE lost_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, lostId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<LostItem> getLostItemsByUser(int userId) throws SQLException {
        List<LostItem> items = new ArrayList<>();
        String query = "SELECT * FROM lost_items WHERE user_id = ?";
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
    public List<LostItem> getAllActiveLostItems() throws SQLException {
        List<LostItem> items = new ArrayList<>();
        String query = "SELECT * FROM lost_items WHERE status = 'active'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapResultSetToEntity(rs));
            }
        }
        return items;
    }

    @Override
    public boolean updateLostItemStatus(int lostId, LostStatus status) throws SQLException {
        String query = "UPDATE lost_items SET status = ? WHERE lost_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.name().toLowerCase());
            stmt.setInt(2, lostId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteLostItem(int lostId) throws SQLException {
        String query = "DELETE FROM lost_items WHERE lost_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, lostId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<LostItem> searchLostItems(String keyword, String category, String location) throws SQLException {
        List<LostItem> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM lost_items WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ? OR brand LIKE ? OR model LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category.trim());
        }
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location_lost LIKE ?");
            params.add("%" + location.trim() + "%");
        }
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToEntity(rs));
                }
            }
        }
        return items;
    }
}
