package events;

import events.literalevents.BlockBreakEvent;
import events.literalevents.BlockPlaceEvent;
import world.blocks.Cube;
import world.player.Player;

public class EventFactory {
    public static BlockBreakEvent blockBreakEvent(Player player, Cube cube){
        return new BlockBreakEvent(cube, player);
    }

    public static BlockPlaceEvent blockPlaceEvent(Player player, Cube cube){
        return new BlockPlaceEvent(cube,player);
    }
}
