package networks;

public class PacketPlayerHealth {
    public int id;
    public int newHealth;
    public int gameId;

    public PacketPlayerHealth() {}

    public PacketPlayerHealth(int id, int newHealth){
        this.id = id;
        this.newHealth = newHealth;
    }
}
