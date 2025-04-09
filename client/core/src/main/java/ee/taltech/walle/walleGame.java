package ee.taltech.walle;

import Network.*;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.walle.Screens.MenuScreen;
import ee.taltech.walle.Screens.Playscreen;

import java.io.IOException;
import java.util.HashMap;

public class walleGame extends Game {
    public static final int V_WIDTH = 480;
    public static final int V_HEIGHT = 360;
    public static final float PPM = 100;// Pixels Per Meter — jääb samaks, nagu sul algselt oli
    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short BULLET_BIT = 4;
    public static final short WALL_BIT = 8;
    public static final short ENEMY_BIT = 16;
    public static final short BRIDGE_BIT = 32;

    public SpriteBatch batch;
    public Client client;
    public HashMap<Integer, Player> players = new HashMap<>();
    public int gameId;
    Playscreen playscreen;

    public Playscreen getPlayscreen() {
        return playscreen;
    }
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
        kryo.register(PacketDisconnect.class);

        kryo.register(BulletData.class);
        kryo.register(PacketBulletDestroy.class);
        kryo.register(PacketDisconnect.class);
        kryo.register(PacketIsSinglePlayer.class);
        kryo.register(PacketIsMultiPlayer.class);
        kryo.register(PacketGameId.class);

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
                }

                if (object instanceof PacketPosition packet) {

                    if (players.containsKey(packet.id)) {
                        Player player = players.get(packet.id);
                        player.x = packet.x;
                        player.y = packet.y;
                    }
                }
                if (object instanceof BulletData packet) {
                    playscreen.createRemoteBullet(packet);
                }

                if (object instanceof PacketGameId packet) {
                    gameId = packet.getGameId();
                }

                if (object instanceof PacketBulletDestroy packet) {
                    playscreen.removeRemoteBullet(packet.bulletId);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("MÄNGIJA LAHKUS: " + connection.getID());
                players.remove(connection.getID());
            }
        });

        // Ava mängu põhiekraan
        setScreen(new MenuScreen(this, client));
        playscreen = new Playscreen(this, client);
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
