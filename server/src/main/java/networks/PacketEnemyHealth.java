package networks;

public class PacketEnemyHealth {
    public int id;       // Boti ID
    public int health;   // Praegune elude arv
    public int gameId;   // Mängu ID

    public PacketEnemyHealth() {}  // KryoNet nõuab tühja konstruktorit

    public PacketEnemyHealth(int id, int health, int gameId) {
        this.id = id;
        this.health = health;
        this.gameId = gameId;
    }
}
