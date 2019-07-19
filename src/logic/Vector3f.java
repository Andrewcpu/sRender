package logic;

public class Vector3f {
    private float x;
    private float y;
    private float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3f(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void multiply(double n){
        this.x *= n;
        this.y *= n;
        this.z *= n;
    }

    public void divide(double n){
        multiply( 1.0 / n);
    }

    private void add(double n){
        this.x += n;
        this.y += n;
        this.z += n;
    }

    public void subtract(double n){
        add(-n);
    }

    public void add(Vector3f vector3f){
        this.x += vector3f.x;
        this.y += vector3f.y;
        this.z += vector3f.z;
    }

    public void subtract(Vector3f vector3f){
        this.x -= vector3f.x;
        this.y -= vector3f.y;
        this.z -= vector3f.z;
    }

    public void multiply(Vector3f vector3f){
        this.x *= vector3f.x;
        this.y *= vector3f.y;
        this.z *= vector3f.z;
    }

    public void divide(Vector3f vector3f){
        this.x /= vector3f.x;
        this.y /= vector3f.y;
        this.z /= vector3f.z;
    }

}
