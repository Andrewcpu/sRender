package world.player;

import javafx.geometry.Point3D;
import logic.Vector3f;
import render.Camera;
import render.Renderer;
import world.Chunk;
import world.HealthedEntity;
import world.Location;
import world.entities.Entity;
import world.player.inventory.Inventory;
import world.player.inventory.ItemStack;


public class Player extends HealthedEntity {
    private Camera camera;

    private Inventory inventory;





    public Player(double x, double y, double z){
        this.camera = new Camera(x,y,z);
        this.inventory = new Inventory();
    }


    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Point3D getLocation(){
        return camera.getLocation();
    }

    public void sendMessage(String s){

    }

    public void teleport(Location location){
        camera.setX(location.getX());
        camera.setY(location.getY());
        camera.setZ(location.getZ());
    }

    public void teleport(Entity entity){
        teleport(entity.getLocation());
    }

    public Vector3f getVelocity(){
        return camera.getPhysicsBody().getVelocity();
    }

    public void setVelocity(Vector3f vel){
        camera.getPhysicsBody().setVelocity(vel);
    }

    public ItemStack getItemInHand(){
        return inventory.getSelectedItem();
    }

    public void give(ItemStack it){
        inventory.addItem(it);
    }






    public void tick(){
        camera.tick();
    }



}
