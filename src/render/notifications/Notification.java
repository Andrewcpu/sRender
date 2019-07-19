package render.notifications;

import java.awt.*;

public class Notification {
    private NotificationStatus status = NotificationStatus.WAITING;
    private String text;
    private Color color = new Color(0,0,0,150);

    public Notification(String text) {
        this.text = text;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
