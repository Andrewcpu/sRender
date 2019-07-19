package render.particles;

import javafx.geometry.Point3D;
import logic.CoordinateUtilities;
import render.Camera;
import render.RenderBlock;
import render.RenderType;
import render.Renderer;
import world.blocks.Cube;

import java.awt.*;

public class Particle {
    private int maxLife = 500;
    private int ticks = 0;

    private Color color;
    private double x, y, z;

    private double PARTICLE_SIZE;

    private Point3D[] nodes = new Point3D[4];


    private boolean orientation = Math.random() < 0.5; // TRUE = X ORIENTED, FALSE = Z

    private double[] movement = new double[3];

    private double speed = 1;

    /*
    I'm going to do a hacky thing to get these to render, future Andrew I'm sorry but right now I'm too lazy to rewrite the rendering engine.
     */

    public Particle(Color color, double x, double y, double z) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.z = z;
        PARTICLE_SIZE = 10 * Math.random();
        updateNodes();

        speed = 1;
        updateMovement();

    }

    public void updateMovement(){
        movement[0] = 0 * speed;
        movement[1] = 0.5 * speed;
        movement[2] = 0 * speed;
    }

    public double[] getMovement() {
        return movement;
    }

    public void setMovement(double[] movement) {
        this.movement = movement;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void updateNodes(){
        nodes = new Point3D[4];
        if(orientation){
            nodes[0] = new Point3D(getX(), getY(), getZ());
            nodes[1] = new Point3D(getX() + PARTICLE_SIZE, getY(), getZ());
            nodes[2] = new Point3D(getX() + PARTICLE_SIZE, getY() + PARTICLE_SIZE, getZ());
            nodes[3] = new Point3D(getX(), getY() + PARTICLE_SIZE, getZ());
        }
        else
        {
            nodes[0] = new Point3D(getX(), getY(), getZ());
            nodes[1] = new Point3D(getX() , getY(), getZ()+ PARTICLE_SIZE);
            nodes[2] = new Point3D(getX() , getY() + PARTICLE_SIZE, getZ()+ PARTICLE_SIZE);
            nodes[3] = new Point3D(getX(), getY() + PARTICLE_SIZE, getZ());
        }


    }

    public RenderBlock getRenderBlock(Camera camera){
        Point3D[] points = new Point3D[nodes.length];
        double[] translation = {camera.getX(),camera.getY(),camera.getZ()};

        for(int n = 0; n<nodes.length; n++){
            Point3D nodeA = nodes[n];
            if(nodeA == null) continue;
            Point3D node = new Point3D(nodeA.getX() - translation[0], nodeA.getY() - translation[1], nodeA.getZ() - translation[2] );


            double[] point = rotationMatrix(node, camera);

            points[n] = new Point3D(point[0]  , point[1]  ,point[2]);
        }

        int[][] sides = new int[1][4];
        sides[0] = new int[]{0,1,2,3};

        return new RenderBlock(points, sides, new Color[]{color}, null, RenderType.PARTICLE);
    }

    public double[] rotationMatrix(Point3D node, Camera camera){
        double[] pointA = CoordinateUtilities.rotateAroundY3D(node, camera.getSideRotation());
        double[] point = CoordinateUtilities.rotateAroundX3D(new Point3D(pointA[0], pointA[1], pointA[2]), camera.getVerticalRotation());
        return point;
    }
    public Point3D rotationMatrix3DPoint(Point3D node, Camera camera){
        double[] matrix = rotationMatrix(node, camera);
        return new Point3D(matrix[0], matrix[1], matrix[2]);
    }

    public int getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(int maxLife) {
        this.maxLife = maxLife;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void tick(){
        this.x += movement[0];
        this.y += movement[1];
        this.z += movement[2];

        updateNodes();
        ticks++;
        double alpha = ticks * 255 / maxLife;
        alpha = 255 - alpha;
        if(alpha > 255) alpha = 255;
        if(alpha < 0 ) alpha = 0;
        setColor(new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), (int)alpha));
    }
}
