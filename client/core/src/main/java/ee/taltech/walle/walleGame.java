package ee.taltech.walle;

import Network.PacketPosition;
import Network.PacketUpdatePlayers;
import Network.Player;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.walle.Screens.Playscreen;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

// Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class walleGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(walleGame.class);

    public static final int V_WIDTH = 360;
    public static final int V_HEIGHT = 240;
    public static final float PPM = 100; // Pixels Per Meter

    public SpriteBatch batch;
    public Client client;
    public Map<Integer, Player> players = new HashMap<>();

    @Override
    public void create() {
        // Loo vajalikud elemendid
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
            // Kontrolli ühendust serveri protidega
            client.connect(5000, "193.40.255.32", 8081, 8081);
            logger.info("Ühendus serveriga oli edukas.");
        } catch (IOException e) {
            logger.error("Ühenduse loomine ebaõnnestus.", e);
        }

        // Kuula serverilt saadetud andmeid
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketUpdatePlayers packet) {
                    players.putAll(packet.players);
                    logger.info("Uuendatud mängijad saadud: {}", players);
                }

                if (object instanceof PacketPosition packet && players.containsKey(packet.id)) {
                    Player player = players.get(packet.id);
                    player.x = packet.x;
                    player.y = packet.y;
                    logger.info("Mängija uuendatud positsioon: ID={}, X={}, Y={}", packet.id, packet.x, packet.y);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                logger.info("Mängija lahkus: {}", connection.getID());
                players.remove(connection.getID());
            }
        });

        // Ava mängu põhiekraan
        setScreen(new Playscreen(this, client));
    }

    // Tagastab kõik mängijad (kasutatakse Playscreen-is)
    public Map<Integer, Player> getPlayers() {
        return players;
    }
    // Render meetod ajutiselt pole vajalik ehk eemaldasin

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        if (client != null) {
            client.stop();
        }
    }
}


