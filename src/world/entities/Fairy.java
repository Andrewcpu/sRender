package world.entities;

import logic.CoordinateUtilities;
import logic.Vector3f;
import render.Camera;
import render.RenderBlock;
import render.RenderShape;
import render.Renderer;
import render.particles.Particle;
import world.Location;
import world.blocks.Cube;

import java.awt.*;

public class Fairy extends Entity {

    private double speed = 1;

    private double sideRotation = Math.random() * Math.toRadians(360);
    private double verticalRotation = Math.random() * Math.toRadians(360);

    private Color color;
    private int alpha = 255;

    public Fairy(Location location) {
        super(location, EntityType.FAIRY);
        this.color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255), alpha);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSideRotation() {
        return sideRotation;
    }

    public void setSideRotation(double sideRotation) {
        this.sideRotation = sideRotation;
    }

    public double getVerticalRotation() {
        return verticalRotation;
    }

    public void setVerticalRotation(double verticalRotation) {
        this.verticalRotation = verticalRotation;
    }

    @Override
    public void tick(){

        double tX =  Math.sin(getSideRotation()) * speed;
        double tY =  Math.sin(getVerticalRotation()) * speed;
        double tZ =  Math.cos(getSideRotation()) * speed;


        getPhysicsBody().applyForce(new Vector3f(tX, tY, tZ));

        if(Math.random() < 0.02){
            sideRotation += Math.toRadians(Math.random() * 180);
        }
        if(Math.random() < 0.02){
            sideRotation -= Math.toRadians(Math.random() * 180);
        }
        if(Math.random() < 0.01){
            verticalRotation += Math.toRadians(Math.random() * 180);
        }
        if(Math.random() < 0.01){
            verticalRotation -= Math.toRadians(Math.random() * 180);
        }


        if(Math.random() < 0.05 && alpha > 0){
            Particle particle = new Particle(color, getLocation().getX(), getLocation().getY(), getLocation().getZ());
            particle.setMovement(new double[]{0,-1,0});
            Renderer.getInstance().registerParticle(particle);
        }

        
        while(verticalRotation < 0)
            verticalRotation += Math.toRadians(360);
        while(verticalRotation > Math.toRadians(360))
            verticalRotation -= Math.toRadians(360);
        
        while(sideRotation < 0)
            sideRotation += Math.toRadians(360);
        while(sideRotation > Math.toRadians(360))
            sideRotation -= Math.toRadians(360);
        
        super.tick();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public RenderBlock getRenderBlock(Camera camera){
        return new RenderShape(getLocation().getX(),getLocation().getY(),getLocation().getZ(), 5, 5, color).getRenderBlock(camera);
    }
}
