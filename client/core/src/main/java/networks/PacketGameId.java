package networks;

public class PacketGameId {
    public int getGameId() {
        return gameId;
    }
    int gameId;
    public PacketGameId(int gameId) {
        this.gameId = gameId;
    }
    public PacketGameId() {}
}
