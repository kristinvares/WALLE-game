package networks;

public class Player {
    public int id;         // Mängija ID
    public float x, y;     // Mängija koordinaadid
    public String name;    // Mängija nimi (valikuline)
    public int gameId;
    public int health = 100;

    // === Tühi konstruktor (Kryo jaoks kohustuslik) ===
    public Player() {}

    // Konstruktor andmete määramiseks
    public Player(int id, float x, float y, String name, int gameId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
        this.gameId = gameId;
    }
}
