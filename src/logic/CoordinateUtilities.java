package logic;

import javafx.geometry.Point3D;
import render.Renderer;
import render.Camera;

import java.awt.*;

import static render.Renderer.DIMENSION;

public class CoordinateUtilities {
    public static void printCartesian2D(double[] a){
        printCartesian2D(a[0],a[1]);
    }

    private static void printCartesian2D(double x, double y){
        System.out.println("(" + x + ", " + y + ")");
    }

    private static void printPolar2D(double r, double theta){
        System.out.println("R: " + r + ", THETA: " + Math.toDegrees(theta ));
    }
    public static void printPolar2D(double[] a){
        printPolar2D(a[0],a[1]);
    }

    private static int getQuadrant(double x, double y){
        if( x < 0 && y > 0)
            return 2;
        if(x <0 && y < 0)
            return 3;
        if(x > 0 && y < 0)
            return 4;
        return 1;
    }

    public static double[] to2DPolarCoordinate(double x, double y){
        double theta = x == 0 ? Math.PI / 2.0 : Math.atan(y / x);
        double[] a = new double[]{Math.sqrt(x * x + y * y), theta};
        int quad = getQuadrant(x,y);
        if(quad == 2 || quad == 3)
            a[1] += Math.toRadians(180);
        if(quad == 4)
            a[1] += Math.toRadians(360);

        return a;
    }

    public static double[] to2DCartesianCoordinate(double r, double theta){
        return new double[]{r * Math.cos(Math.toRadians(theta)), r * Math.sin(Math.toRadians(theta))};
    }

    public static double toDegree(double outsideDegree){
        while (outsideDegree > 360)
            outsideDegree-=360;
        while(outsideDegree < 0)
            outsideDegree+=360;
        return outsideDegree;
    }


    private static double[] to3DSphereicalCoordinates(double x, double y, double z){
        double a[] = {0,0,0};
        a[0] = Math.sqrt(x * x + y * y + z * z);
        a[1] = Math.acos(z / a[0]);
        a[2] = Math.atan(y / z);
        return a;
    }

    private static double[] to3DCartesianCoordinates(double r, double t, double a){
        double[] ret = {0,0,0};
        ret[0] = r * Math.cos(t) * Math.sin(a);
        ret[1] = r * Math.sin(t) * Math.sin(a);
        ret[2] = r * Math.cos(t);
        return ret;
    }


    //vertical and horizontal rotations ARE IN RADIANS IDIOT, DONT MESS IT UP.
    public static double[] rotateCartesian3DPointAroundOrigin(double x, double y, double z, double vertical, double horizontal){
        double[] spherical = to3DSphereicalCoordinates(x,y,z);
        return to3DCartesianCoordinates(spherical[0], spherical[1] + vertical, spherical[2] + horizontal);
    }

    //r value overrides distance from origin,
    public static double[] rotateCartesian3DPointAroundOrigin(double x, double y, double z, double r, double vertical, double horizontal){
        double[] spherical = to3DSphereicalCoordinates(x,y,z);
        return to3DCartesianCoordinates(r, spherical[1] + vertical, spherical[2] + horizontal);
    }



    public static double[] getDistanceToOrigin(Point3D point3D){
        return getDistanceToOrigin(new double[]{point3D.getX(),point3D.getY(),point3D.getZ()});
    }

    private static double[] getDistanceToOrigin(double[] coordinates){
        return coordinates;
    }

    //fov in degrees
    public double get2DChordLength(double fov, double distance){
        return distance * Math.sin(Math.toRadians(fov / 2.0));
    }



    public static double[] map3DTo2DScreen(double x, double y, double z, double planeDistance, double fov){
        Point3D point = getPointOnPlane(x,y,z,planeDistance, fov);
        return new double[]{point.getX(),point.getY()};

    }

    private static  Point3D getPointOnPlane(double x, double y, double z, double planeZ, double fov){

        Point3D vector = new Point3D(x,y,z);

        double mod = planeZ / vector.getZ();

        vector = new Point3D(vector.getX() * mod, vector.getY() * mod, planeZ);

        double d = (getDimensions(fov, planeZ));

        //d(x) = dimensions

        double modifier = (double) DIMENSION / (d / 2.0);

        return  new Point3D(vector.getX() * modifier,vector.getY() * modifier,planeZ);
    }


    private static double getDimensions(double fov, double planeZ){
        return planeZ * Math.tan(Math.toRadians(fov / 2.0)) * 2;
    }




    public static boolean hasLineOfSight(Point3D target){
        return false;
    }

    public static boolean isVisible(Point3D checkMe, double fov){
        if(checkMe == null || checkMe.getZ() < 1) return false;
        double length = checkMe.distance(0,0,0);
        if(length > Renderer.RENDER_DISTANCE) return false;
        double inverseLength = 1.0 / length;
        Point3D direction = new Point3D(checkMe.getX() * inverseLength, checkMe.getY() * inverseLength, checkMe.getZ() * inverseLength);
        Point3D norm2 = new Point3D(0,0,1);
        double dot = norm2.dotProduct(direction);
        return dot >= Math.cos(Math.toRadians(fov / 2.0));
    }


    public static  Point3D getVectorBetweenPoints(Point3D from, Point3D to){
        return  new Point3D(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
    }



    public static double[] rotateAroundY3D(Point3D point, double t){
        double x = point.getX();
        double z = point.getZ();

        double nX =  x * Math.cos(t) - z * Math.sin(t);
        double nZ =  z * Math.cos(t) + x * Math.sin(t);
        return new double[]{nX, point.getY(), nZ};
    }

    public static double[] rotateAroundX3D (Point3D point, double theta) {
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double y = point.getY();
        double z = point.getZ();
        double nY = y * cosTheta - z * sinTheta;
        double nZ = z * cosTheta + y * sinTheta;
        return new double[]{point.getX(),nY,nZ};
    }


    public static double[] rotateAroundZ3D (Point3D point, double theta) {
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double x = point.getY();
        double y = point.getZ();
        double nX = x * cosTheta - y * sinTheta;
        double nY = y * cosTheta + x * sinTheta;
        return new double[]{nX,nY,point.getZ()};
    }


    public static void main(String[] args) {
        Point3D a = new Point3D(3.0,3.0,3.0);
        for(double f = 0; f<360; f++){
            double[] cartesian = rotateAroundY3D(a, Math.toRadians(f));
            System.out.println(cartesian[0] + ", " + cartesian[1] + ", " + cartesian[2]);
        }
    }

    public static Point3D directionToMovement(Direction direction){
        switch (direction){
            case DOWN:
                return new Point3D(0,-1,0);
            case UP:
                return new Point3D(0,1,0);
            case NORTH:
                return new Point3D(0,0,1);
            case SOUTH:
                return new Point3D(0,0,-1);
            case EAST:
                return new Point3D(1,0,0);
            case WEST:
                return new Point3D(-1, 0, 0);
        }
        return new Point3D(0,0,0);
    }



    public static Direction getOppositeDirection(Direction d){
        if(d == Direction.UP)return Direction.DOWN;
        if(d == Direction.DOWN) return Direction.UP;
        if(d == Direction.NORTH) return Direction.SOUTH;
        if(d == Direction.SOUTH) return  Direction.NORTH;
        if(d == Direction.EAST) return Direction.WEST;
        if(d == Direction.WEST) return  Direction.EAST;
        return Direction.NORTH;
    }



    public static Point3D visionMatrixTransformation(Point3D nodeA, Camera camera){

        double[] translation = {camera.getX(),camera.getY(),camera.getZ()};

            Point3D node = new Point3D(nodeA.getX() - translation[0], nodeA.getY() - translation[1], nodeA.getZ() - translation[2] );
            double[] pointA = CoordinateUtilities.rotateAroundY3D(node, camera.getSideRotation());
            double[] point = CoordinateUtilities.rotateAroundX3D(new Point3D(pointA[0], pointA[1], pointA[2]), camera.getVerticalRotation());
            return new Point3D(point[0], point[1], point[2]);

    }

    public static Point scale3DTo2D(Point3D b, Camera camera){
        double[] bPoints = CoordinateUtilities.map3DTo2DScreen(b.getX(),b.getY(),b.getZ(), 10, camera.getFov());
        bPoints[0] *= camera.getFocalLength();
        bPoints[1] *= -camera.getFocalLength();
        bPoints[0] += DIMENSION / 2.0;
        bPoints[1] += DIMENSION / 2.0;

        return new Point((int)bPoints[0], (int)bPoints[1]);
    }



    public static Point3D averagePoint(Point3D[] point3DS){
        Point3D total = new Point3D(0,0,0);
        for(Point3D p : point3DS){
            total = total.add(p.multiply( 1.0 / point3DS.length));
        }
        return total;
    }

    
    public static Color fadeFromTo(Color a, Color b, double x){

        double percent = Math.abs(Math.cos(Math.PI / 2 * x / 100.0));


        double red = (a.getRed()) * (1.0 - percent) + (b.getRed() * percent);
        double green = (a.getGreen()) * (1.0 - percent) + (b.getGreen() * percent);
        double blue = (a.getBlue()) * (1.0 - percent) + (b.getBlue() * percent);
        double alpha = (a.getAlpha()) * (1.0 - percent) + (b.getAlpha() * percent);


        return new Color((int)red,(int)green,(int)blue, (int)alpha);
    }


    public static Color fadeFromToPercent(Color a, Color b, double percent){


        double red = (a.getRed()) * (1.0 - percent) + (b.getRed() * percent);
        double green = (a.getGreen()) * (1.0 - percent) + (b.getGreen() * percent);
        double blue = (a.getBlue()) * (1.0 - percent) + (b.getBlue() * percent);
        double alpha = (a.getAlpha()) * (1.0 - percent) + (b.getAlpha() * percent);

        return new Color((int)red,(int)green,(int)blue, (int)alpha);
    }
    
}
