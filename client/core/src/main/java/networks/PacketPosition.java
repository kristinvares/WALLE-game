package networks;

public class PacketPosition {
    public int id;
    public float x, y;

    // Tühi konstruktor (vajalik Kryo jaoks)
    public PacketPosition() {}

    public PacketPosition(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}

