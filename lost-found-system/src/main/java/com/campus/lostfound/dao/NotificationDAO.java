package com.campus.lostfound.dao;

import com.campus.lostfound.model.Notification;
import java.sql.SQLException;
import java.util.List;

public interface NotificationDAO {
    int create(Notification notification) throws SQLException;
    Notification getById(int notificationId) throws SQLException;
    List<Notification> getByUser(int userId) throws SQLException;
    boolean markAsRead(int notificationId) throws SQLException;
    boolean markEmailSent(int notificationId) throws SQLException;
    boolean markSmsSent(int notificationId) throws SQLException;
    int getUnreadCount(int userId) throws SQLException;
}
