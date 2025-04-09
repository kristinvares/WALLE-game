package Network;

public class PacketDisconnect {
    public int id;

    // tuhi konstruktor (vajalik Kryo jaoks)
    public PacketDisconnect() {}

    public PacketDisconnect(int id) {
        this.id = id;
    }
}
