package org.techive.onlinecanteenorder.model;

public class MyNotification {
    private String title;
    private String message;
    private String receiverid;
    private String iconUrl;
    private String action;
    private String actionDestination;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDestination() {
        return actionDestination;
    }

    public void setActionDestination(String actionDestination) {
        this.actionDestination = actionDestination;
    }

    public String getReceiverId() {
        return receiverid;
    }

    public void setReceiverId(String receiverId) {
        this.receiverid = receiverId;
    }
}
