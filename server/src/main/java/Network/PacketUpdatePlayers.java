package Network;

import java.util.Map;
import java.util.HashMap;

public class PacketUpdatePlayers {
    public Map<Integer, Player> players = new HashMap<>();

    // Tühi konstruktor (vajalik Kryo jaoks)
    public PacketUpdatePlayers() {}

    // Konstruktor playeri updateimiseks
    public PacketUpdatePlayers(Map<Integer, Player> players) {
        this.players = players;
    }
}
