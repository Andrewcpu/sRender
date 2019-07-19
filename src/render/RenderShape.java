package render;

import javafx.geometry.Point3D;
import logic.CoordinateUtilities;

import java.awt.*;

public class RenderShape {



    private static final int FRONT_TOP_LEFT = 0;
    private static final int FRONT_TOP_RIGHT = 1;
    private static final int FRONT_BOTTOM_LEFT = 2;
    private static final int FRONT_BOTTOM_RIGHT = 3;
    private static final int BACK_TOP_LEFT = 4;
    private static final int BACK_TOP_RIGHT = 5;
    private static final int BACK_BOTTOM_LEFT= 6;
    private static final int BACK_BOTTOM_RIGHT= 7;

    // CUBE
    private double x;
    private double y;
    private double z;
    private double width;
    private double height;


    private Color color;

    private final Point3D[] nodes = new Point3D[8];
    private final int[][] sides = new int[6][4];


    private boolean fadeInDistance = true;




    public RenderShape(double x, double y, double z, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.color = color;
        configureNodes();
    }

    public boolean isFadeInDistance() {
        return fadeInDistance;
    }

    public void setFadeInDistance(boolean fadeInDistance) {
        this.fadeInDistance = fadeInDistance;
    }

    public void configureNodes(){
        sides[0] = new int[]{FRONT_TOP_RIGHT,FRONT_TOP_LEFT,FRONT_BOTTOM_LEFT,FRONT_BOTTOM_RIGHT}; // front
        sides[1] = new int[]{FRONT_TOP_LEFT,FRONT_BOTTOM_LEFT,BACK_BOTTOM_LEFT,BACK_TOP_LEFT}; // left
        sides[2] = new int[]{FRONT_TOP_RIGHT,FRONT_BOTTOM_RIGHT,BACK_BOTTOM_RIGHT,BACK_TOP_RIGHT}; // right
        sides[3] = new int[]{BACK_TOP_RIGHT,BACK_TOP_LEFT,BACK_BOTTOM_LEFT,BACK_BOTTOM_RIGHT}; // back
        sides[4] = new int[]{FRONT_TOP_RIGHT,FRONT_TOP_LEFT,BACK_TOP_LEFT,BACK_TOP_RIGHT}; // top
        sides[5] = new int[]{FRONT_BOTTOM_RIGHT,FRONT_BOTTOM_LEFT,BACK_BOTTOM_LEFT,BACK_BOTTOM_RIGHT}; // bottom



        nodes[0] = getFrontTopLeft();
        nodes[1] = getFrontTopRight();
        nodes[2] = getFrontBottomLeft();
        nodes[3] = getFrontBottomRight();
        nodes[4] = getBackTopLeft();
        nodes[5] = getBackTopRight();
        nodes[6] = getBackBottomLeft();
        nodes[7] = getBackBottomRight();
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

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    private Point3D getFrontTopLeft(){
        return new Point3D(getX(), getY(), getZ());
    }
    private Point3D getFrontTopRight(){
        return new Point3D(getX() + getWidth(), getY(), getZ());
    }
    private Point3D getFrontBottomLeft(){
        return new Point3D(getX(), getY() + getHeight(), getZ());
    }
    private Point3D getFrontBottomRight(){
        return new Point3D(getX() + getWidth(), getY() + getHeight(), getZ());
    }
    private Point3D getBackTopLeft(){
        return new Point3D(getX(), getY(), getZ() + getWidth());
    }
    private Point3D getBackTopRight(){
        return new Point3D(getX() + getWidth(), getY(), getZ() + getWidth());
    }
    private Point3D getBackBottomLeft(){
        return new Point3D(getX(), getY() + getHeight(), getZ() + getWidth());
    }
    private Point3D getBackBottomRight(){
        return new Point3D(getX() + getWidth(), getY() + getHeight(), getZ() + getWidth());
    }


    public RenderBlock getRenderBlock(Camera camera){
        Point3D[] points = new Point3D[nodes.length];

        double[] translation = {camera.getX(),camera.getY(),camera.getZ()};

        for(int n = 0; n<nodes.length; n++){
            double[] point = rotationMatrix(new Point3D(nodes[n].getX() - translation[0], nodes[n].getY() - translation[1], nodes[n].getZ() - translation[2] ), camera);
            points[n] = new Point3D(point[0]  , point[1]  ,point[2]);
        }
        RenderBlock bl = new RenderBlock(points, sides, new Color[]{color, color, color, color, color, color}, null, RenderType.SHAPE);
        bl.setFadeInDistance(fadeInDistance);
        return bl;
    }


    public double[] rotationMatrix(Point3D node, Camera camera){
        double[] pointA = CoordinateUtilities.rotateAroundY3D(node, camera.getSideRotation());
        double[] point = CoordinateUtilities.rotateAroundX3D(new Point3D(pointA[0], pointA[1], pointA[2]), camera.getVerticalRotation());
        return point;
    }
}
