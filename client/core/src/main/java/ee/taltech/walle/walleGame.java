package ee.taltech.walle;

import ee.taltech.walle.Sprites.PlayerSprite;
import networks.*;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class walleGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(walleGame.class);
    public static final int V_WIDTH = 480;
    public static final int V_HEIGHT = 360;
    public static final float PPM = 100;
    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short BULLET_BIT = 4;
    public static final short WALL_BIT = 8;
    public static final short ENEMY_BIT = 16;
    public static final short EXIT_BIT = 32;

    public SpriteBatch batch;

    public Client getClient() {
        return client;
    }

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
        client = new Client(1024 * 1024, 1024 * 1024);
        client.start();

        registerClientClasses(client.getKryo());
        connectToServer();
        setupClientListener();

        setScreen(new MenuScreen(this, client));
        playscreen = new Playscreen(this, client);
    }

    private void registerClientClasses(Kryo kryo) {
        kryo.register(PacketPosition.class);
        kryo.register(Player.class);
        kryo.register(PacketUpdatePlayers.class);
        kryo.register(HashMap.class);
        kryo.register(BulletData.class);
        kryo.register(PacketBulletDestroy.class);
        kryo.register(PacketDisconnect.class);
        kryo.register(PacketIsSinglePlayer.class);
        kryo.register(PacketIsMultiPlayer.class);
        kryo.register(PacketGameId.class);
        kryo.register(PacketPlayerHealth.class);
        kryo.register(int[].class);
        kryo.register(int[][].class);
        kryo.register(PacketEnemyPosition.class);
        kryo.register(PacketEnemyHealth.class);
        kryo.register(PacketEnemyHit.class);
    }

    private void connectToServer() {
        try {
            client.connect(5000, "localhost", 8080, 8081);
            logger.error("\u00dcHENDUS SERVERIGA LOODUD!");
        } catch (IOException e) {
            logger.error("\u00dchenduse loomine eba\u00f5nnestus: {}", e.getMessage());
        }
    }

    private void setupClientListener() {
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketUpdatePlayers packet) {
                    players.putAll(packet.players);
                } else if (object instanceof PacketPosition packet && players.containsKey(packet.id)) {
                    Player player = players.get(packet.id);
                    player.x = packet.x;
                    player.y = packet.y;
                } else if (object instanceof BulletData packet) {
                    playscreen.createRemoteBullet(packet);
                } else if (object instanceof PacketGameId packet) {
                    gameId = packet.getGameId();
                } else if (object instanceof PacketBulletDestroy packet) {
                    playscreen.removeRemoteBullet(packet.bulletId);
                } else if (object instanceof PacketEnemyPosition packet && playscreen != null) {
                    playscreen.updateEnemyPosition(packet);
                } else if (object instanceof PacketEnemyHealth packet && playscreen != null) {
                    playscreen.handleEnemyHealthUpdate(packet);
                }
                else if (object instanceof PacketPlayerHealth packet && playscreen != null) {
                    if (packet.id == client.getID()) {
                        // Kohalik mängija
                        playscreen.getPlayer().setHealth(packet.newHealth);
                    } else {
                        // Teised mängijad
                        PlayerSprite remote = playscreen.getRemotePlayerById(packet.id);
                        if (remote != null) {
                            remote.setHealth(packet.newHealth);
                        }
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                logger.info("M\u00c4NGIJA LAHKUS: {}", connection.getID());
                players.remove(connection.getID());
            }
        });
    }

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
