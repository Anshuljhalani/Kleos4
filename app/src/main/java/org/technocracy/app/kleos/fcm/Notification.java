package org.technocracy.app.kleos.fcm;

/**
 * Created by MOHIT on 15-Sep-16.
 */
public class Notification {

    String title;
    String message;
    String imageUrl;
    String createdAt;

    public Notification() {

    }

    public Notification(String title, String message, String imageUrl, String createdAt) {
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
