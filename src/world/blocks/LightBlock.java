package world.blocks;

import javafx.geometry.Point3D;
import logic.CoordinateUtilities;
import logic.Vector3f;
import render.Renderer;
import world.LightSource;
import world.Location;
import world.entities.Fairy;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LightBlock extends Cube {
    private LightSource source;
    public LightBlock(double x, double y, double z, double intensity) {
        super(x, y, z, Renderer.CUBE_DEFAULT_SIZE, Renderer.CUBE_DEFAULT_SIZE, BlockType.LIGHT);
        source = new LightSource(x + Renderer.CUBE_DEFAULT_SIZE / 2,y + Renderer.CUBE_DEFAULT_SIZE / 2,z + Renderer.CUBE_DEFAULT_SIZE / 2,intensity, Color.WHITE, this);
        Renderer.getInstance().registerLightSource(source);
    }

    @Override
    public void setColor(Color color, int side) {
        super.setColor(color, side);
        source.setLightColor(color);
    }

    public LightSource getSource() {
        return source;
    }

    private int ticks = 0;

    public int getTicks() {
        return ticks;
    }

    @Override
    public void tick(){
        ticks++;
        if(Math.random() < 0.03) {
            double r = Math.random() * 25;
            if (Math.random() < 0.5)
                r = -r;
            getChunk().getWorld().spawnParticle(getMiddleOfCube().add(r, getHeight() / 2 + 2, r), getSource().getLightColor());
        }
        if(Math.random() < 0.002){
            int radius = 50 + (int)(25 * Math.sin(Math.toRadians(getTicks())));
            for(double y = 0; y <= 100 * 10; y+=2.5) {
                final double yy = y;

                Executors.newSingleThreadScheduledExecutor().schedule(()->{
                    double x = radius * Math.cos(Math.toRadians((yy)));
                    double z = radius * Math.sin(Math.toRadians(yy));

                    Point3D p = getMiddleOfCube().add(x,yy,z);

                    Point3D c = CoordinateUtilities.getVectorBetweenPoints(getMiddleOfCube(), p).normalize();


                    getChunk().getWorld().spawnParticle(getMiddleOfCube().add(x, yy, z), getSource().getLightColor()).setMovement(new double[]{c.getX(),c.getY(),c.getZ()});
                }, 5 * (int)yy, TimeUnit.MILLISECONDS);


            }
        }

        if(Math.random() < 0.00003){
            Fairy fairy = new Fairy(new Location(getLocation().getX(),getLocation().getY(),getLocation().getZ(), Renderer.getInstance().getWorld()));
            fairy.setColor(source.getCube().getColor(0));
            Renderer.getInstance().getWorld().getEntities().add(fairy);
        }
    }

}
