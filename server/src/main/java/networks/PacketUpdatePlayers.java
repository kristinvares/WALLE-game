package networks;

import java.util.HashMap;

public class PacketUpdatePlayers {
    public HashMap<Integer, Player> players = new HashMap<>();

    // Tühi konstruktor (vajalik Kryo jaoks)
    public PacketUpdatePlayers() {}

    public PacketUpdatePlayers(HashMap<Integer, Player> players) {
        this.players = players;
    }
}
