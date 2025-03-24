package ee.taltech.WALLE;

import Network.PacketPosition;
import Network.PacketUpdatePlayers;
import Network.Player;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.WALLE.Screens.Playscreen;

import java.io.IOException;
import java.util.HashMap;

public class WALLEGame extends Game {
    public static final int V_WIDTH = 360;
    public static final int V_HEIGHT = 240;
    public static final float PPM = 100; // Pixels Per Meter — jääb samaks, nagu sul algselt oli

    public SpriteBatch batch;
    public Client client;
    public HashMap<Integer, Player> players = new HashMap<>();

    @Override
    public void create() {
        batch = new SpriteBatch();
        client = new Client();
        client.start();

        // Registreeri Kryo jaoks kõik vajalikud klassid
        Kryo kryo = client.getKryo();
        kryo.register(PacketPosition.class);
        kryo.register(Player.class);
        kryo.register(PacketUpdatePlayers.class);
        kryo.register(HashMap.class);

        try {
            client.connect(5000, "localhost", 8080, 8081);
            System.out.println("ÜHENDUS SERVERIGA LOODUD!");
        } catch (IOException e) {
            System.err.println("Ühenduse loomine ebaõnnestus: " + e.getMessage());
        }

        // Kuula serverilt saadetud andmeid
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketUpdatePlayers packet) {
                    players.putAll(packet.players);
                    System.out.println("UUENDATUD MÄNGIJAD SAADUD: " + players);
                }

                if (object instanceof PacketPosition packet) {
                    if (players.containsKey(packet.id)) {
                        Player player = players.get(packet.id);
                        player.x = packet.x;
                        player.y = packet.y;
                        System.out.println("MÄNGIJA UUENDATUD POSITSIOON: ID=" + packet.id + " X=" + packet.x + " Y=" + packet.y);
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("MÄNGIJA LAHKUS: " + connection.getID());
                players.remove(connection.getID());
            }
        });

        // Ava mängu põhiekraan
        setScreen(new Playscreen(this, client));
    }

    // Tagastab kõik mängijad (kasutatakse Playscreen-is)
    public HashMap<Integer, Player> getPlayers() {
        return players;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        if (client != null) {
            client.stop();
        }
    }
}


