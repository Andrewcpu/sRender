package render;

import javafx.geometry.Point3D;
import logic.CoordinateUtilities;
import logic.Direction;
import world.LightSource;
import world.blocks.Cube;

import java.awt.*;

import static render.Renderer.DIMENSION;

class RenderSide implements Comparable<RenderSide>{
    private Point3D a, b, c, d;


    private int n = 0; // n as in the original iteration where n was looped through as a size of sides, this became problematic when you started using getColor(n) since the new order isnt tthe same as the old order but the color indexes never changed.


    private Direction direction;


    private Color color;

    private Cube parentCube = null;

    public RenderSide(Point3D a, Point3D b, Point3D c, Point3D d, Color color, Direction direction, Cube parentCube) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.color = color;
        this.direction = direction;
        this.parentCube = parentCube;
        setN();
    }

    public Cube getParentCube() {
        return parentCube;
    }

    private void setN(){
        if(direction== Direction.SOUTH)
            n = 0;
        if(direction == Direction.WEST)
            n = 1;
        if(direction == Direction.EAST)
            n = 2;
        if(direction == Direction.NORTH)
            n = 3;
        if(direction == Direction.DOWN)
            n = 4;
        if(direction == Direction.UP)
            n = 5;
    }

    public int getN() {
        return n;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    private Point3D getAveragePoint(){
        return CoordinateUtilities.averagePoint(new Point3D[]{a, b, c, d});
    }

    public double distance(){
        return getAveragePoint().distance(0,0,0);
    }

    public Point3D getA() {
        return a;
    }

    public void setA(Point3D a) {
        this.a = a;
    }

    public Point3D getB() {
        return b;
    }

    public void setB(Point3D b) {
        this.b = b;
    }

    public Point3D getC() {
        return c;
    }

    public void setC(Point3D c) {
        this.c = c;
    }

    public Point3D getD() {
        return d;
    }

    public void setD(Point3D d) {
        this.d = d;
    }

    public Color getColor() {
        return color;
    }

    private void setColor(Color color) {
        this.color = color;
    }



    public void applyLighting(){ // light source location is the actual position of
        LightSource closestLightSource = parentCube.getClosestLightSource()[getN()];

        if(closestLightSource == null) {
//            setColor(CoordinateUtilities.fadeFromToPercent(getColor(), Renderer.getInstance().skyColor, 0.10));
            return;
        }

        double dist = CoordinateUtilities.visionMatrixTransformation(closestLightSource.getLocation(), Renderer.getInstance().getCamera()).distance(getAveragePoint());
        double factor = (closestLightSource.getIntensity() - dist) / (1.5 * closestLightSource.getIntensity()) ;
        if(factor < 0) return;


        setColor(CoordinateUtilities.fadeFromToPercent(getColor(), closestLightSource.getLightColor(), factor));
    }

    @Override
    public int compareTo(RenderSide o) {
        return Double.compare(o.distance(), distance());
    }


    private double[][] getViewMatrixTransformedPoints(Camera camera){
        double focalLength = camera.getFocalLength();
        double[] aPoints = CoordinateUtilities.map3DTo2DScreen(a.getX(),a.getY(),a.getZ(), 10, camera.getFov());
        aPoints[0] *= focalLength;
        aPoints[1] *= -focalLength;
        aPoints[0] += DIMENSION / 2.0;
        aPoints[1] += DIMENSION / 2.0;

        double[] bPoints = CoordinateUtilities.map3DTo2DScreen(b.getX(),b.getY(),b.getZ(), 10, camera.getFov());
        bPoints[0] *= focalLength;
        bPoints[1] *= -focalLength;
        bPoints[0] += DIMENSION / 2.0;
        bPoints[1] += DIMENSION / 2.0;

        double[] cPoints = CoordinateUtilities.map3DTo2DScreen(c.getX(),c.getY(),c.getZ(), 10, camera.getFov());
        cPoints[0] *= focalLength;
        cPoints[1] *= -focalLength;
        cPoints[0] += DIMENSION / 2.0;
        cPoints[1] += DIMENSION / 2.0;

        double[] dPoints = CoordinateUtilities.map3DTo2DScreen(d.getX(),d.getY(),d.getZ(), 10, camera.getFov());
        dPoints[0] *= focalLength;
        dPoints[1] *= -focalLength;
        dPoints[0] += DIMENSION / 2.0;
        dPoints[1] += DIMENSION / 2.0;



        return new double[][]{aPoints,bPoints,cPoints,dPoints};

    }


    public Polygon getDrawingPolygon(Camera camera){
        double[][] view = getViewMatrixTransformedPoints(camera);
        return new Polygon(new int[]{(int)view[0][0],(int)view[1][0],(int)view[2][0],(int)view[3][0]}, new int[]{(int)view[0][1],(int)view[1][1],(int)view[2][1],(int)view[3][1]}, 4);
    }


}
