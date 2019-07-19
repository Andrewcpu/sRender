package events;

import events.literalevents.BlockBreakEvent;
import events.literalevents.BlockPlaceEvent;
import render.Renderer;
import render.notifications.Notification;
import render.notifications.NotificationManager;
import world.blocks.LightBlock;
import world.LightSource;

class EventListener {

    public void handle(Event event){
        switch (event.getType()){
            case BLOCK_BREAK_EVENT:
                blockBreakEvent((BlockBreakEvent)event);
                break;
            case BLOCK_PLACE_EVENT:
                blockPlaceEvent((BlockPlaceEvent)event);
                break;
        }
    }

    private void blockBreakEvent(BlockBreakEvent event){
        if(event.getCube() instanceof LightBlock){
            LightSource source = ((LightBlock)event.getCube()).getSource();
            Renderer.getInstance().unregisterLightSource(source);
            NotificationManager.getInstance().queue(new Notification("Destroyed a light."));
        }
    }

    private void blockPlaceEvent(BlockPlaceEvent event){
        NotificationManager.getInstance().queue(new Notification("Placed a " + event.getCube().getBlockType().toString()));
    }
}
