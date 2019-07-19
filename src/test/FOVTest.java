package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class FOVTest extends JFrame {
    public static void main(String[] args) {
        new FOVTest();
    }
    private final Canvas canvas = new Canvas();

    private FOVTest(){
        setBounds(0,0,500,500);
        add(canvas);
        canvas.setBounds(getBounds());
        setVisible(true);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->canvas.repaint(), 20, 20, TimeUnit.MILLISECONDS);
    }
}
class Canvas extends JComponent implements MouseMotionListener {
    public Canvas(){
        addMouseMotionListener(this);
    }
    private Point point = new Point(0,0);
    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        point = e.getPoint();
    }

    private double rotation = 0;


    private double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        while(rotation > 360)
            rotation -= 360;
        while( rotation < 0)
            rotation += 360;
        this.rotation = rotation;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {


        double[] polar = toPolarCoordinate(point.x - 250,point.y - 250);

        polar[1] -= Math.toRadians(getRotation());


        double[] cart = toCartesianCoordinate(polar[0], polar[1]);
        this.point = new Point((int)cart[0] + 250, (int)-cart[1] + 250);
        System.out.println(point.toString());
    }

    @Override
    public void paint(Graphics g){

        int offset = 250;
        double solidFOV = 60;


        double bottomAvg = (rotation) - solidFOV / 2 ;
        double topAvg= (rotation) + solidFOV / 2 ;


        for(int i = 0; i<getWidth(); i++){
            for(int j = 0; j<getHeight(); j++){
                double[] polar = toPolarCoordinate(i - offset, -j + offset);

                    polar[1] = Math.toDegrees(polar[1]) ;

                    if(isBetween(polar[1],bottomAvg,topAvg, solidFOV))
                        g.setColor(Color.GREEN);
                    else
                        g.setColor(Color.BLACK);



                    g.fillRect(i,j,1,1);
            }
        }
        double pointerDistance = 100;
        double[] p1 = (toCartesianCoordinate(pointerDistance, (360 - rotation - solidFOV / 2.0))); // bottom line
        double[] p2 = (toCartesianCoordinate(pointerDistance, (360 - rotation + solidFOV / 2.0))); //top line
        double[] p3 = (toCartesianCoordinate(pointerDistance * 2, (360 - rotation))); //mid line
        double[] p4 = (toCartesianCoordinate(pointerDistance, (360 - Math.toDegrees(toPolarCoordinate(point.x - offset, -point.y + offset)[1]))));

        double mouseAngle =  ( Math.toDegrees(toPolarCoordinate(point.x - offset, -point.y + offset)[1]));

        g.setColor(Color.RED);
        g.fillOval((int)p1[0] + offset - 5,(int)p1[1] + offset - 5, 10, 10);
        g.drawLine((int)p1[0] + offset,(int)p1[1] + offset, offset, offset);
        g.fillOval((int)p2[0] + offset - 5,(int)p2[1] + offset - 5, 10, 10);
        g.drawLine((int)p2[0] + offset,(int)p2[1] + offset, offset, offset);
        g.fillOval((int)p3[0] + offset - 5,(int)p3[1] + offset - 5, 10, 10);
        g.drawLine((int)p3[0] + offset,(int)p3[1] + offset, offset, offset);

        g.drawLine((int)p2[0] + offset,(int)p2[1] + offset, (int)p1[0] + offset,  (int)p1[1] + offset);


        g.drawOval((int)p4[0] + offset, (int)p4[1] + offset, 5, 5);



        //mouse line

        g.drawLine(offset, offset, (int)p4[0] + offset, (int)p4[1] + offset);



        //bounding circle
        g.drawOval((int)(-pointerDistance) + offset, (int)(-pointerDistance) + offset, (int)pointerDistance * 2, (int)pointerDistance * 2);


        double d = pointerDistance * Math.sin(Math.toRadians(solidFOV / 2.0)); // chord length, which gives us the distance out at a given point with a radius of pointerDistance


      //  double f = pointerDistance - Math.sqrt(pointerDistance * pointerDistance - (d) * (d));



        //double dist = pointerDistance - f; // middle of chord is this distance from the center of the circle

       // double[] chordMiddleLocation = toCartesianCoordinate(dist, 360 - rotation);
      //  g.drawOval((int)chordMiddleLocation[0] + offset, (int)chordMiddleLocation[1] + offset, 5, 5);



        double a = (d * Math.sqrt(3) / Math.cos(Math.toRadians(-mouseAngle + rotation)));
        a = (d * Math.sqrt(3) / Math.cos(Math.toRadians(-mouseAngle + rotation)));


        double[] chordPoint = toCartesianCoordinate(a, mouseAngle);

        g.drawOval((int)chordPoint[0] + 250, ((int)-chordPoint[1]) + 250, 5, 5);




        g.setColor(Color.WHITE);
        g.drawString(bottomAvg + "", 10, 10);
        g.drawString(topAvg + "", 10, 30);
        g.drawString(rotation + "", 10, 50);
        g.drawString((toPolarCoordinate(point.x,-point.y + offset)[1]) + "", 10, 70);
        g.drawString((d * 2)+ " = Chord length", 10, 90);
       // g.drawString(f + " = Sagitta", 10, 110);

        if(this.rotation > 360)
        this.rotation -= 360;
        if(this.rotation < 0)
            this.rotation += 360;
     //    this.rotation += .1;
      //   if(this.rotation > 360) this.rotation -= 360;
    }


    private boolean isBetween(double a, double b, double c, double fov){
        if(b + fov > 360){
            if( a >= b || a <= c - 360)
                return true;
            return false;
        }
        else{
            if( a >= b && a <= c)
                return true;
        }
        if(b < 0){
            if(360 + b <= a && a <= 360)
                return true;
        }
        return false;

    }


    public void printCartesian(double[] a){
        printCartesian(a[0],a[1]);
    }

    private void printCartesian(double x, double y){
        System.out.println("(" + x + ", " + y + ")");
    }

    private void printPolar(double r, double theta){
        System.out.println("R: " + r + ", THETA: " + Math.toDegrees(theta ));
    }
    public void printPolar(double[] a){
        printPolar(a[0],a[1]);
    }

    private int getQuadrant(double x, double y){
        if( x < 0 && y > 0)
            return 2;
        if(x <0 && y < 0)
            return 3;
        if(x > 0 && y < 0)
            return 4;
        return 1;
    }

    private double[] toPolarCoordinate(double x, double y){
        double theta = x == 0 ? Math.PI / 2.0 : Math.atan(y / x);
        double[] a = new double[]{Math.sqrt(x * x + y * y), theta};
        int quad = getQuadrant(x,y);
        if(quad == 2 || quad == 3)
            a[1] += Math.toRadians(180);
        if(quad == 4)
            a[1] += Math.toRadians(360);

        return a;
    }

    private double[] toCartesianCoordinate(double r, double theta){
        return new double[]{r * Math.cos(Math.toRadians(theta)), r * Math.sin(Math.toRadians(theta))};
    }



    public double toDegree(double outsideDegree){
        while (outsideDegree > 360)
            outsideDegree-=360;
        while(outsideDegree < 0)
            outsideDegree+=360;
        return outsideDegree;
    }

    public double[] getVectorToPoint(double x, double y, double j, double k){
        double [] b = new double[]{(j-x),(k - y)};
        double mod = Math.sqrt(b[0] * b[0] + b[1] * b[1]);
        return new double[]{b[0] / mod, b[1] / mod};
    }

}
