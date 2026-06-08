package com.campus.lostfound.service;

import com.campus.lostfound.model.Notification;

public interface NotificationObserver {
    void onNotification(Notification notification);
}
