package networks;

public class PacketPosition {
    public int id;
    public int gameId;
    public float x;
    public float y;

    // Tühi konstruktor (vajalik Kryo jaoks)
    public PacketPosition() {}

    public PacketPosition(int id, float x, float y, int gameId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.gameId = gameId;
    }
}
