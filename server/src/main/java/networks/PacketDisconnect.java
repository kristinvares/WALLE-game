package networks;

public class PacketDisconnect {
    public int id;

    // Kryo jaoks vajalik tühi konstruktor
    public PacketDisconnect() {}

    public PacketDisconnect(int id) {
        this.id = id;
    }
}
