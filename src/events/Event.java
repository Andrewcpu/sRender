package events;

public class Event {
    private EventType type;

    private boolean cancelled = false;

    protected Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
