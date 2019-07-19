package world;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import logic.Vector3f;
import render.Renderer;

public class PhysicsBody {
    private double x, y, z;
    private final double width;
    private final double height;
    private final double depth;

    private Vector3f velocity = new Vector3f(0,0,0);

    private boolean solid = false;


    public PhysicsBody(double x, double y, double z, double width, double height, double depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void applyForce(Vector3f vector3f){
        setVelocity(vector3f);
    }
    public void setVelocity(Vector3f vector3f){
        velocity = vector3f;
    }


    public void tick(){
        velocity.multiply(0.96);


        if(isSolid())
        {
            if(collides(x + velocity.getX(), y + velocity.getY(), z + velocity.getZ()))
            {
//                for(double velX = velocity.getX(); (velocity.getX() < 0 ? velX <= 0 : velX >= 0); velX -= velocity.getX() < 0 ? -0.1 : 0.1){
//                    for(double velY = velocity.getY(); (velocity.getY() < 0 ? velY <= 0 : velY >= 0); velY -= velocity.getY() < 0 ? -0.1 : 0.1){
//                        for(double velZ = velocity.getZ(); (velocity.getZ() < 0 ? velZ <= 0 : velZ >= 0) ; velZ -= velocity.getZ() < 0 ? -0.1 : 0.1){
//                            if(!collides(x + velX, y + velY, z + velZ)){
//                                velocity = new Vector3f(velX, velY, velZ);
//                            }
//                        }
//                    }
//                }
            }
        }

        move(x + velocity.getX(), y + velocity.getY(), z + velocity.getZ());
    }

    public boolean collides(double x, double y, double z){
        return Renderer.getInstance().getWorld().getBlockAt(x,y,z) != null;
    }




    private void move(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BoundingBox moveTarget(Point3D point3D){

        double x = point3D.getX();
        double y = point3D.getY();
        double z = point3D.getZ();

        return new BoundingBox(x - width / 2,y - height / 2,z - depth / 2,width,height,depth);
    }

    public BoundingBox getBounds(){
        return new BoundingBox(x - width / 2,y - height / 2,z - depth / 2,width,height,depth);
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
}
