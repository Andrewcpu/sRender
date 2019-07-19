package events.literalevents;

import events.Event;
import events.EventType;
import world.blocks.Cube;
import world.player.Player;

public class BlockPlaceEvent extends Event {
    private Cube cube;
    private Player player;


    public BlockPlaceEvent(Cube cube, Player player) {
        super(EventType.BLOCK_PLACE_EVENT);
        this.cube = cube;
        this.player = player;
    }

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
