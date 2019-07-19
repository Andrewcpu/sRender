package world;

import javafx.geometry.Point3D;
import world.blocks.Cube;

import java.awt.*;

public class LightSource {
    private double x, y, z;
    private Point3D location;
    private double intensity;
    private Color lightColor;

    private final Cube cube;

    private int tick = 0;

    public LightSource(double x, double y, double z, double intensity, Color lightColor, Cube cube) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.location = new Point3D(x,y,z);
        this.intensity = intensity;
        this.lightColor = lightColor;
        this.cube = cube;
    }

    public Cube getCube() {
        return cube;
    }

    public Color getLightColor() {
//        y=0.95-0.05\cos\left(x\right)

        double mod = .95 - 0.05 * Math.cos(0.01 * tick);
        return new Color((int)(mod * lightColor.getRed()), (int)(mod * 0.85 * lightColor.getGreen()), (int)(mod * 0.85 * lightColor.getBlue()),(int)(lightColor.getAlpha()));
    }

    public void setLightColor(Color lightColor) {
        this.lightColor = lightColor;
    }

    public void setLocation(Point3D location){
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.location = new Point3D(x,y,z);
    }

    public Point3D getLocation(){
        return location;
    }

    private double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    private double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    private double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getIntensity() {
        return intensity + (75 * Math.cos(0.01 * tick));
    }

    public void tick(){
        tick++;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double distanceTo(Point3D point3D){
        return point3D.distance(getLocation());
    }
    public double distanceTo(double x, double y, double z){
        Point3D a = new Point3D(getX(),getY(),getZ());
        return a.distance(x,y,z);
    }
}
