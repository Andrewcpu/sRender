package render.notifications;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

    private static final int DURATION = 500;


    private final List<Notification> notifications = new ArrayList<>();

    private static NotificationManager instance;

    private int timing = 0;

    public static NotificationManager getInstance(){
        return instance;
    }

    public NotificationManager() {
        instance = this;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void queue(Notification notification){
        this.notifications.add(notification);
    }

    public Notification getCurrentNotification(){
        return notifications.size() == 0 ? null : notifications.get(0);
    }

    public double getOpacity(){
        // 500
        // 0 - 100, 400 - 500
        if(timing < 100){
            return timing / 100.0;
        }
        if(timing < 400){
            return 1.0;
        }
        return 1 + (400 - timing) / 100.0;
    }

    public void tick(){
        if(notifications.size() > 0)
            timing++;
        if(timing > DURATION && getCurrentNotification() != null){
            timing = 0;
            getCurrentNotification().setStatus(NotificationStatus.COMPLETED);
            notifications.remove(getCurrentNotification());
            if(getCurrentNotification() != null){
                getCurrentNotification().setStatus(NotificationStatus.ACTIVE);
            }
        }

    }

}
