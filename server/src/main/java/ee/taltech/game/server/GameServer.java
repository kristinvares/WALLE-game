package ee.taltech.game.server;

import networks.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;

import ee.taltech.game.server.Bot;

// Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

    private Server server;
    private HashMap<Integer, Player> players = new HashMap<>();
    private HashMap<Integer, BulletData> activeBullets = new HashMap<>();
    private Queue<Integer> availableIds = new LinkedList<>();
    private AtomicInteger nextBulletId = new AtomicInteger(1); // Unikaalsed ID-d
    private HashMap<Integer, GameInstance> gameInstances= new HashMap<>();
    private AtomicInteger gameInstanceId = new AtomicInteger(1);
    private Set<Integer> readyPlayers = new HashSet<>();

    public GameServer() {

        // see kood on liiga pikk, aga hetkeseisuga ei vota seda riski et seda muuta
        server = new Server(1024 * 1024, 1024 * 1024);
        server.start();
        int firstId = gameInstanceId.getAndIncrement();
        gameInstances.put(firstId, new GameInstance(firstId));

        Kryo kryo = server.getKryo();
        kryo.register(PacketPosition.class);
        kryo.register(Player.class);
        kryo.register(PacketUpdatePlayers.class);
        kryo.register(HashMap.class);
        kryo.register(BulletData.class);
        kryo.register(PacketBulletDestroy.class);
        kryo.register(PacketDisconnect.class);
        kryo.register(BulletData.class);
        kryo.register(PacketBulletDestroy.class);
        kryo.register(PacketIsSinglePlayer.class);
        kryo.register(PacketIsMultiPlayer.class);
        kryo.register(PacketGameId.class);
        kryo.register(PacketPlayerHealth.class);

        kryo.register(PacketMapData.class);
        kryo.register(int[].class);
        kryo.register(int[][].class);

        kryo.register(PacketEnemyPosition.class);

        try {
            server.bind(8080, 8081);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                logger.info("CLIENT CONNECTED: {}", connection.getID());
            }

            @Override
            public void received(Connection connection, Object object) {

                try {
                    if (object instanceof PacketMapData packet) {
                        logger.info("🗺️ Server sai kaardi! Suurus: {} x {}", packet.mapData.length, packet.mapData[0].length);

                        int clientId = connection.getID();
                        for (GameInstance instance : gameInstances.values()) {
                            if (instance.getPlayers().containsKey(clientId)) {
                                instance.setCollisionMap(packet.mapData);
                                logger.info("✅ Collision kaart salvestatud mängule ID-ga {}", instance.getGameId());
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    logger.error("❌ Viga paketi töötlemisel!", e);
                }

                if (object instanceof PacketPosition packet) {
                    if (!readyPlayers.contains(packet.id)) {
                        logger.warn("Hoiatus: Mängija {} saatis positsiooni enne, kui ta oli mängus valmis.", packet.id);
                        return; // Ära töötle positsiooni
                    }
                    GameInstance instance = gameInstances.get(packet.gameId);
                    if (instance != null) {
                        HashMap<Integer, networks.Player> players = instance.getPlayers();
                        Player player = players.get(packet.id);
                        if (player != null) {
                            player.x = packet.x;
                            player.y = packet.y;
                            sendUpdatedPlayers(players);
                        }
                    }
                }
                if (object instanceof PacketIsSinglePlayer packet) {
                    logger.info("Klient {} tahab sp mängu", connection.getID());
                    int spGameId = gameInstanceId.getAndIncrement();
                    GameInstance singlePlayerGame = new GameInstance(spGameId);
                    gameInstances.put(spGameId, singlePlayerGame);
                    Player newPlayer = new Player(connection.getID(), 0, 0, "Player_" + connection.getID(), spGameId);
                    singlePlayerGame.addPlayer(newPlayer);
                    readyPlayers.add(connection.getID()); // Märgi mängija valmisolek
                    connection.sendTCP(new PacketGameId(spGameId));

                    Bot bot = new Bot(singlePlayerGame, 30 * 16 / 100f, 15 * 16 / 100f);
                    bot.setId(singlePlayerGame.getBotIdCounter().getAndIncrement());
                    singlePlayerGame.addBot(bot);
                    logger.info("🤖 BOT lisatud mängu ID-ga {} ja positsiooniga ({}, {})", spGameId, bot.getX(), bot.getY());
                }
                if (object instanceof PacketIsMultiPlayer packet) {
                    logger.info("Klient {} tahab MP mängu", connection.getID());
                    int mpGameId = firstId;
                    GameInstance mpGame = gameInstances.get(mpGameId);
                    Player newPlayer = new Player(connection.getID(), 0, 0, "Player_" + connection.getID(), mpGameId);
                    mpGame.addPlayer(newPlayer);
                    readyPlayers.add(connection.getID()); // Märgi mängija valmisolek
                    connection.sendTCP(new PacketGameId(mpGameId));

                    Bot bot = new Bot(mpGame, 30 * 16 / 100f, 15 * 16 / 100f);
                    bot.setId(mpGame.getBotIdCounter().getAndIncrement());
                    mpGame.addBot(bot);
                    logger.info("🤖 BOT lisatud MP mängu ID-ga {} ja positsiooniga ({}, {})", bot.getId(), bot.getX(), bot.getY());
                }
                if (object instanceof BulletData packet) {
                    int realBulletId = nextBulletId.getAndIncrement(); // Uus serveri bullet ID
                    packet.bulletId = realBulletId;  // Kirjuta peale
                    int gameId = packet.gameID;
                    GameInstance instance = gameInstances.get(gameId);

                    if (instance != null) {
                        instance.addBullet(new BulletData(packet.x, packet.y, packet.shooterID, gameId)); // Lisa mängu bullet
                        packet.bulletId = realBulletId;
                        for (Player player : instance.getPlayers().values()) {
                            server.sendToUDP(player.id, packet);
                        }
                    }
                }

                if (object instanceof PacketBulletDestroy packet) {
                    GameInstance instance = gameInstances.get(packet.gameId);
                    if (instance != null) {
                        instance.removeBullet(packet.bulletId);

                        for (Player player : instance.getPlayers().values()) {
                            server.sendToUDP(player.id, packet);
                        }
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                logger.info("CLIENT DISCONNECTED: {}", connection.getID());


                int disconnectedId = connection.getID();

                // Itereerime kõik gameInstances läbi ja otsime mängija selle ID-ga
                for (GameInstance instance : gameInstances.values()) {
                    HashMap<Integer, networks.Player> players = instance.getPlayers();

                    Integer playerIdToRemove = null;

                    for (Map.Entry<Integer, networks.Player> entry : players.entrySet()) {
                        if (entry.getValue().id == disconnectedId) {
                            playerIdToRemove = entry.getKey();
                            break;
                        }
                    }

                    if (playerIdToRemove != null) {
                        players.remove(playerIdToRemove);
                        sendUpdatedPlayers(players);
                        break;
                    }
                }
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (GameInstance instance : gameInstances.values()) {
                    // Uuenda bottide positsiooni
                    instance.updateBots(1f);

                    // Saada igale mängijale info boti positsiooni kohta
                    for (PacketEnemyPosition packet : instance.getEnemyPositions()) {
                        for (Player player : instance.getPlayers().values()) {
                            server.sendToUDP(player.id, packet);
                        }
                    }
                }
            }
        }, 1000, 1000);
    }

    private void sendUpdatedPlayers(HashMap<Integer, networks.Player> players) {
        PacketUpdatePlayers packet = new PacketUpdatePlayers(players);
        for (Player player : players.values()) {
            server.sendToUDP(player.id, packet);
        }
    }
    public static void main(String[] args) {
        new GameServer();
    }
}

