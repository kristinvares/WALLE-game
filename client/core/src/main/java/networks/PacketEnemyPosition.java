package networks;

public class PacketEnemyPosition {
    public int id; // Bot ID
    public float x;
    public float y;
    public int gameId;

    public PacketEnemyPosition() {} // vajalik Kryo jaoks

    public PacketEnemyPosition(int id, float x, float y, int gameId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.gameId = gameId;
    }
}
