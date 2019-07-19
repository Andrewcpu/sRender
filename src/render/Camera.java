package render;

import javafx.geometry.Point3D;
import logic.Vector3f;
import world.PhysicsBody;

public class Camera {
    private double x, y, z;

    private double sideRotation = 0;
    private double verticalRotation = 0;


    private double speed = 1;

    private double fov = 90;

    private double focalLength = 4.0 / 5.0;

    private final PhysicsBody physicsBody;

    private Point3D renderInstance = new Point3D(0,0,0);





    public Camera(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        physicsBody = new PhysicsBody(x,y + 100,z, 50,150,50);
        physicsBody.setSolid(true);
    }



    public PhysicsBody getPhysicsBody() {
        return physicsBody;
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
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

    public double getSideRotation() {
        return sideRotation;
    }

    public void setSideRotation(double sideRotation) {
        this.sideRotation = sideRotation;
        sync();
    }

    public double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    public void forward(){


        double tX =  Math.sin(getSideRotation()) * speed;
        double tY =  Math.sin(getVerticalRotation()) * speed;
        double tZ =  Math.cos(getSideRotation()) * speed;


        physicsBody.applyForce(new Vector3f(tX, tY, tZ));


        handleMovement();

    }

    private int ticks = 0;

    public void tick(){
        ticks++;
        getPhysicsBody().tick();
        handleMovement();

    }

    public int getTicks() {
        return ticks;
    }

    private void handleMovement(){
        this.x = physicsBody.getX();
        this.y = physicsBody.getY();
        this.z = physicsBody.getZ();
    }


    public Point3D getForward(){
        return new Point3D(Math.sin(getSideRotation()), Math.sin(getVerticalRotation()), Math.cos(getSideRotation()));
    }

    public void left(){
        double tX = -Math.sin(getSideRotation() + Math.toRadians(90)) * speed;
        double tZ = -Math.cos(getSideRotation() + Math.toRadians(90)) * speed;
        physicsBody.applyForce(new Vector3f(tX, physicsBody.getVelocity().getY(), tZ));
        handleMovement();

    }
    public void right(){
        double tX = Math.sin(getSideRotation() + Math.toRadians(90)) * speed;
        double tZ = Math.cos(getSideRotation() + Math.toRadians(90)) * speed;
        physicsBody.applyForce(new Vector3f(tX, physicsBody.getVelocity().getY(), tZ));
        handleMovement();

    }

    public void up(){
        physicsBody.applyForce(new Vector3f(physicsBody.getVelocity().getX(), speed, physicsBody.getVelocity().getZ()));
        handleMovement();

    }

    public void down(){
        physicsBody.applyForce(new Vector3f(physicsBody.getVelocity().getX(), -speed, physicsBody.getVelocity().getZ()));
        handleMovement();

    }



    public void backwards(){
        double tX = -Math.sin(getSideRotation()) * speed;
        double tZ = -Math.cos(getSideRotation()) * speed;
        physicsBody.applyForce(new Vector3f(tX, physicsBody.getVelocity().getY(), tZ));
        handleMovement();

    }


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getVerticalRotation() {
        return verticalRotation;
    }

    public void setVerticalRotation(double verticalRotation) {
        this.verticalRotation = verticalRotation;
        sync();

    }

    private void sync(){
        this.verticalRotation = Math.toDegrees(this.verticalRotation);
        while(verticalRotation > 360)
            verticalRotation -= 360;
        while(verticalRotation < 0)
            verticalRotation += 360;
        this.sideRotation = Math.toDegrees(this.sideRotation);
        while(sideRotation > 360)
            sideRotation -= 360;
        while(sideRotation < 0)
            sideRotation += 360;
        this.verticalRotation = Math.toRadians(this.verticalRotation);
        this.sideRotation = Math.toRadians(this.sideRotation);
    }

    public Point3D getLocation(){
        return new Point3D(getX(),getY() + 3 * Math.cos(Math.toRadians(ticks)),getZ());
    }

    public Point3D getRenderInstance(){
        return this.renderInstance;
    }

    public void setRenderInstance(){
        this.renderInstance = new Point3D(getX(),getY() + 3 * Math.cos(Math.toRadians(ticks)),getZ());
    }
}
