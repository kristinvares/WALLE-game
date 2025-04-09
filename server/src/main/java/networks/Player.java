package networks;

public class Player {
    public int id;         // Mängija ID
    public float x, y;     // Mängija koordinaadid
    public String name;    // Mängija nimi (valikuline)

    // === Tühi konstruktor (Kryo jaoks kohustuslik) ===
    public Player() {}

    // Konstruktor andmete määramiseks
    public Player(int id, float x, float y, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }
}
