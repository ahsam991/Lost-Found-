package com.campus.lostfound.dao;

import com.campus.lostfound.model.LostItem;
import com.campus.lostfound.model.LostStatus;
import java.sql.SQLException;
import java.util.List;

public interface LostItemDAO {
    int reportLostItem(LostItem item) throws SQLException;
    LostItem getLostItemById(int lostId) throws SQLException;
    List<LostItem> getLostItemsByUser(int userId) throws SQLException;
    List<LostItem> getAllActiveLostItems() throws SQLException;
    boolean updateLostItemStatus(int lostId, LostStatus status) throws SQLException;
    boolean deleteLostItem(int lostId) throws SQLException;
    List<LostItem> searchLostItems(String keyword, String category, String location) throws SQLException;
}
