package networks;

public class PacketIsSinglePlayer {
    public int clientId;
    public int[][] mapData;

    public PacketIsSinglePlayer(Integer clientId, int[][] mapData) {
        this.clientId = clientId;
        this.mapData = mapData;
    }

    public PacketIsSinglePlayer() {}
}
