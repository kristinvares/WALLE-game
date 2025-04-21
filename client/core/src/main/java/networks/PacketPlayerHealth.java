package networks;

public class PacketPlayerHealth {
    public int id;
    public int newHealth;

    public PacketPlayerHealth() {}

    public PacketPlayerHealth(int id, int newHealth){
        this.id = id;
        this.newHealth = newHealth;
    }
}
