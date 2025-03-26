package Network;

public class PacketPosition {
    public int id;
    public float x;
    public float y;

    // Tühi konstruktor (vajalik Kryo jaoks)
    public PacketPosition() {}

    // Konstruktor positsiooni kordinaatide saatmiseks
    public PacketPosition(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
