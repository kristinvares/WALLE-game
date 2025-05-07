package networks;

public class PacketEnemyHit {
    public int enemyId;
    public int gameId;

    public PacketEnemyHit() {}

    public PacketEnemyHit(int enemyId, int gameId) {
        this.enemyId = enemyId;
        this.gameId = gameId;
    }
}
