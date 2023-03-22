package com.bing.lan.invest.domain;

/**
 * Created by lanbing at 2023/3/22 9:53
 */

public class BaseIDEAMessage {

    protected NotificationType notificationType = NotificationType.INFORMATION;

    protected String title = "notice";

    protected String notificationGroupId = "holdingMonitoringGroup";

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotificationGroupId() {
        return notificationGroupId;
    }

    public void setNotificationGroupId(String notificationGroupId) {
        this.notificationGroupId = notificationGroupId;
    }
}
