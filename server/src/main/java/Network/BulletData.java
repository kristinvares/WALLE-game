package Network;

public class BulletData {
    public int bulletId;
    public int shooterID;
    public int gameID;
    public float x, y;
    public float directionX, directionY;

    public BulletData() {}

    public BulletData(float x, float y, int shooterID, int gameID) {
        this.x = x;
        this.y = y;
        this.shooterID = shooterID;
    }
}
