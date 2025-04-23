package networks;

public class PacketIsMultiPlayer {
    public int clientId;
    public int[][] mapData;

    public PacketIsMultiPlayer(Integer clientId, int[][] mapData) {
        this.clientId = clientId;
        this.mapData = mapData;
    }

    public PacketIsMultiPlayer() {}
}
