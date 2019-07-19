package world.entities;

import logic.Vector3f;
import render.Camera;
import render.RenderBlock;
import world.Location;
import world.PhysicsBody;

public class Entity {
    private Location location;
    private EntityType type;
    private PhysicsBody physicsBody;
    public Entity(Location location, EntityType type) {
        this.location = location;
        this.type = type;
        this.physicsBody = new PhysicsBody(getLocation().getX(),getLocation().getY(),getLocation().getZ(),10,10,10);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public PhysicsBody getPhysicsBody() {
        return physicsBody;
    }

    public void setPhysicsBody(PhysicsBody physicsBody) {
        this.physicsBody = physicsBody;
    }

    public void tick(){
        physicsBody.tick();
        handleMovement();
    }

    private void handleMovement(){
        getLocation().update(physicsBody.getX(),physicsBody.getY(),physicsBody.getZ());
    }

    public RenderBlock getRenderBlock(Camera camera){
        return null;
    }
}
