package networks;

public class PacketMapData {
    public int[][] mapData;

    // tuhi konstruktor (vajalik Kryo jaoks)
    public PacketMapData() {}

    public PacketMapData(int[][] mapData) {
        this.mapData = mapData;
    }
}
