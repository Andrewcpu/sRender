package world;

public class HealthedEntity {
    private double health = 1.0;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void damage(double amt){
        this.health -= amt;
    }

    public void damage(double top, double bottom){
        this.health -= top / bottom;
    }
}
