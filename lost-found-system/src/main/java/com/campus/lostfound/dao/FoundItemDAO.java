package com.campus.lostfound.dao;

import com.campus.lostfound.model.FoundItem;
import com.campus.lostfound.model.FoundStatus;
import java.sql.SQLException;
import java.util.List;

public interface FoundItemDAO {
    int reportFoundItem(FoundItem item) throws SQLException;
    FoundItem getFoundItemById(int foundId) throws SQLException;
    List<FoundItem> getFoundItemsByUser(int userId) throws SQLException;
    List<FoundItem> getAllAvailableFoundItems() throws SQLException;
    boolean updateFoundItemStatus(int foundId, FoundStatus status) throws SQLException;
    boolean updateFoundItemStatusAndClaimant(int foundId, FoundStatus status, Integer claimantId) throws SQLException;
    boolean deleteFoundItem(int foundId) throws SQLException;
    List<FoundItem> searchFoundItems(String keyword, String category, String location) throws SQLException;
}
