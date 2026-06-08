package com.campus.lostfound.dao;

import com.campus.lostfound.model.FoundItem;
import com.campus.lostfound.model.FoundStatus;
import org.json.JSONArray;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FoundItemDAOImpl extends BaseDAO<FoundItem> implements FoundItemDAO {

    public FoundItemDAOImpl() {
        super();
    }

    @Override
    protected FoundItem mapResultSetToEntity(ResultSet rs) throws SQLException {
        FoundItem item = new FoundItem();
        item.setFoundId(rs.getInt("found_id"));
        item.setUserId(rs.getInt("user_id"));
        item.setTitle(rs.getString("title"));
        item.setCategory(rs.getString("category"));
        item.setBrand(rs.getString("brand"));
        item.setModel(rs.getString("model"));
        item.setColor(rs.getString("color"));
        item.setSerialNumber(rs.getString("serial_number"));
        item.setDescription(rs.getString("description"));
        item.setLocationFound(rs.getString("location_found"));
        item.setLocationLat(rs.getString("location_lat"));
        item.setLocationLng(rs.getString("location_lng"));
        
        Date dateFoundDb = rs.getDate("date_found");
        if (dateFoundDb != null) {
            item.setDateFound(dateFoundDb.toLocalDate());
        }
        
        item.setSafeKeepingLocation(rs.getString("safe_keeping_location"));
        
        // Deserialize photos
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
        
        item.setStatus(FoundStatus.fromString(rs.getString("status")));
        
        int claimedBy = rs.getInt("claimed_by_id");
        if (!rs.wasNull()) {
            item.setClaimedById(claimedBy);
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
        return "SELECT * FROM found_items";
    }

    @Override
    public int reportFoundItem(FoundItem item) throws SQLException {
        String query = "INSERT INTO found_items (user_id, title, category, brand, model, color, serial_number, description, " +
                       "location_found, location_lat, location_lng, date_found, safe_keeping_location, photos, status, claimed_by_id, created_at, updated_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            stmt.setString(9, item.getLocationFound());
            stmt.setString(10, item.getLocationLat());
            stmt.setString(11, item.getLocationLng());
            
            stmt.setDate(12, item.getDateFound() != null ? Date.valueOf(item.getDateFound()) : Date.valueOf(LocalDate.now()));
            stmt.setString(13, item.getSafeKeepingLocation());
            
            JSONArray jsonArray = new JSONArray(item.getPhotoPaths());
            stmt.setString(14, jsonArray.toString());
            
            stmt.setString(15, item.getStatus().name().toLowerCase());
            
            if (item.getClaimedById() != null) {
                stmt.setInt(16, item.getClaimedById());
            } else {
                stmt.setNull(16, Types.INTEGER);
            }

            stmt.setTimestamp(17, Timestamp.valueOf(item.getCreatedAt() != null ? item.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(18, Timestamp.valueOf(item.getUpdatedAt() != null ? item.getUpdatedAt() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        item.setFoundId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public FoundItem getFoundItemById(int foundId) throws SQLException {
        String query = "SELECT * FROM found_items WHERE found_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, foundId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<FoundItem> getFoundItemsByUser(int userId) throws SQLException {
        List<FoundItem> items = new ArrayList<>();
        String query = "SELECT * FROM found_items WHERE user_id = ?";
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
    public List<FoundItem> getAllAvailableFoundItems() throws SQLException {
        List<FoundItem> items = new ArrayList<>();
        String query = "SELECT * FROM found_items WHERE status = 'available'";
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
    public boolean updateFoundItemStatus(int foundId, FoundStatus status) throws SQLException {
        String query = "UPDATE found_items SET status = ? WHERE found_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.name().toLowerCase());
            stmt.setInt(2, foundId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateFoundItemStatusAndClaimant(int foundId, FoundStatus status, Integer claimantId) throws SQLException {
        String query = "UPDATE found_items SET status = ?, claimed_by_id = ? WHERE found_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status.name().toLowerCase());
            if (claimantId != null) {
                stmt.setInt(2, claimantId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, foundId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteFoundItem(int foundId) throws SQLException {
        String query = "DELETE FROM found_items WHERE found_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, foundId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<FoundItem> searchFoundItems(String keyword, String category, String location) throws SQLException {
        List<FoundItem> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM found_items WHERE 1=1");
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
            sql.append(" AND location_found LIKE ?");
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
