package events;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final EventListener coreEventListener = new EventListener();
    private final List<EventListener> eventListeners = new ArrayList<>();

    private static EventManager instance = null;

    public static EventManager getInstance(){
        return instance;
    }

    public EventManager(){
        instance = this;
    }

    public void registerListener(EventListener eventListener){
        eventListeners.add(eventListener);
    }

    public void throwEvent(Event event){
        for(EventListener eventListener : eventListeners){
            eventListener.handle(event);
        }

        coreEventListener.handle(event);
    }
}
