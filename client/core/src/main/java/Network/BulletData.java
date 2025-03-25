package Network;

public class BulletData {
    public int bulletId;
    public int shooterID;
    public float x, y;
    public float directionX, directionY;

    public BulletData() {}

    public BulletData(float x, float y, int shooterID) {
        this.x = x;
        this.y = y;
        this.shooterID = shooterID;
    }
}


