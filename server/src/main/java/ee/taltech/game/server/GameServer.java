package ee.taltech.game.server;

import networks.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer {
    // Konstruktor käivitab serveri, registreerib klassid ja loob esimese mänguinstantsi
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

    private static Server server;
    private final AtomicInteger nextBulletId = new AtomicInteger(1);
    private final HashMap<Integer, GameInstance> gameInstances = new HashMap<>();
    private final AtomicInteger gameInstanceId = new AtomicInteger(1);
    private final Set<Integer> readyPlayers = new HashSet<>();

    public GameServer() {
        logger.info("Starting Game Server...");

        initializeServer();
        int firstId = gameInstanceId.getAndIncrement();
        gameInstances.put(firstId, new GameInstance(firstId));

        Kryo kryo = server.getKryo();
        registerClasses(kryo);

        bindServer();
        setupListeners(firstId);
        setupBotUpdateLoop();
    }

    private void initializeServer() {
        // Loob KryoNet serveri ja käivitab selle
        server = new Server(1024 * 1024, 1024 * 1024);
        server.start();
    }

    private void registerClasses(Kryo kryo) {
        // Registreerib kõik vajalikud klassid, mida võrgupaketid kasutavad (Kryo jaoks)
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

    private void bindServer() {
        // Seob serveri pordid 8080 (TCP) ja 8081 (UDP) külge
        try {
            server.bind(8080, 8081);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners(int firstId) {
        // Määrab ühenduse, sissetulevate andmete ja katkestuste käsitlejad
        // Listener on peamine koht, kus klientidega suheldakse
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                logger.info("CLIENT CONNECTED: {}", connection.getID());
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketPosition packet) {
                    handlePositionPacket(connection, packet);
                } else if (object instanceof PacketIsSinglePlayer packet) {
                    handleSinglePlayerPacket(connection, packet);
                } else if (object instanceof PacketIsMultiPlayer packet) {
                    handleMultiPlayerPacket(connection, packet, firstId);
                } else if (object instanceof BulletData packet) {
                    handleBulletDataPacket(packet);
                } else if (object instanceof PacketBulletDestroy packet) {
                    handleBulletDestroyPacket(packet);
                } else if (object instanceof PacketEnemyHit packet) {
                    handleEnemyHitPacket(packet);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                logger.info("CLIENT DISCONNECTED: {}", connection.getID());

                int disconnectedId = connection.getID();

                for (GameInstance instance : gameInstances.values()) {
                    HashMap<Integer, networks.Player> instancePlayers = instance.getPlayers();
                    Integer playerIdToRemove = null;

                    for (Map.Entry<Integer, networks.Player> entry : instancePlayers.entrySet()) {
                        if (entry.getValue().id == disconnectedId) {
                            playerIdToRemove = entry.getKey();
                            break;
                        }
                    }

                    if (playerIdToRemove != null) {
                        instancePlayers.remove(playerIdToRemove);
                        sendUpdatedPlayers(instancePlayers);
                        break;
                    }
                }
            }
        });
    }

    private void handlePositionPacket(Connection connection, PacketPosition packet) {
        // Kontrollib, kas mängija on mänguks valmis, ja uuendab tema positsiooni serveris
        if (!readyPlayers.contains(packet.id)) {
            logger.warn("Hoiatus: Mängija {} saatis positsiooni enne, kui ta oli mängus valmis.", packet.id);
            return;
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

    private void handleSinglePlayerPacket(Connection connection, PacketIsSinglePlayer packet) {
        // Loob uue SP mängu ja lisab mängija sinna (kaardi andmed saadetakse kliendilt)
        int spGameId = gameInstanceId.getAndIncrement();
        GameInstance singlePlayerGame = new GameInstance(spGameId);
        singlePlayerGame.setCollisionMap(packet.mapData);

        gameInstances.put(spGameId, singlePlayerGame);

        Player newPlayer = new Player(connection.getID(), 0, 0, "Player_" + connection.getID(), spGameId);
        singlePlayerGame.addPlayer(newPlayer);
        readyPlayers.add(connection.getID());

        connection.sendTCP(new PacketGameId(spGameId));
        logger.info("🎮 Mängija lisatud SP instantsi ID-ga {}", newPlayer.gameId);

        singlePlayerGame.spawnBotIfNeeded();
    }

    private void handleMultiPlayerPacket(Connection connection, PacketIsMultiPlayer packet, int firstId) {
        // Ühendab mängija olemasolevasse MP instantsi
        // Kui kaart puudub, lisab selle saadud mapData põhjal
        int mpGameId = firstId;
        GameInstance mpGame = gameInstances.get(mpGameId);

        if (mpGame.getCollisionMap() == null) {
            mpGame.setCollisionMap(packet.mapData);
            logger.info("✅ MP mängule lisati kaart");
        }

        Player newPlayer = new Player(connection.getID(), 0, 0, "Player_" + connection.getID(), mpGameId);
        mpGame.addPlayer(newPlayer);
        readyPlayers.add(connection.getID());

        connection.sendTCP(new PacketGameId(mpGameId));
        logger.info("🎮 Mängija lisatud MP instantsi ID-ga {}", newPlayer.gameId);

        mpGame.spawnBotIfNeeded();
    }

    private void handleBulletDataPacket(BulletData packet) {
        // Genereerib uue kuuliparameetri ja levitab selle kõikidele mängijatele
        int realBulletId = nextBulletId.getAndIncrement();
        packet.bulletId = realBulletId;
        GameInstance instance = gameInstances.get(packet.gameID);

        if (instance != null) {
            instance.addBullet(new BulletData(packet.x, packet.y, packet.shooterID, packet.gameID));
            for (Player player : instance.getPlayers().values()) {
                server.sendToUDP(player.id, packet);
            }
            for (Bot bot : instance.getBots()) {
                float dx = packet.x - bot.getX();
                float dy = packet.y - bot.getY();
                float distanceSq = dx * dx + dy * dy;

                float hitRadius = 0.1f;

                if (distanceSq < hitRadius * hitRadius) {
                    bot.takeDamage(1);

                    logger.info("[BOT {}] tabatud kuuliga. HP: {}", bot.getId(), bot.getHealth());
                    break;
                }
            }
        }
    }

    private void handleBulletDestroyPacket(PacketBulletDestroy packet) {
        // Eemaldab serveri poolel kuuli ja teatab sellest kõigile mängijatele
        GameInstance instance = gameInstances.get(packet.gameId);
        if (instance != null) {
            instance.removeBullet(packet.bulletId);
            for (Player player : instance.getPlayers().values()) {
                server.sendToUDP(player.id, packet);
            }
        }
    }

    private void handleEnemyHitPacket(PacketEnemyHit packet) {
        GameInstance instance = gameInstances.get(packet.gameId);
        if (instance == null) return;

        Bot bot = null;
        for (Bot b : instance.getBots()) {
            if (b.getId() == packet.enemyId) {
                bot = b;
                break;
            }
        }

        if (bot != null) {
            bot.takeDamage(1); // Lahutab 1 elu

            logger.info("[BOT {}] sai kliendilt hiti. Uus HP: {}", bot.getId(), bot.getHealth());

            // Saada uuendatud HP kõikidele mängijatele
            PacketEnemyHealth hpPacket = new PacketEnemyHealth(bot.getId(), bot.getHealth(), packet.gameId);
            for (Player player : instance.getPlayers().values()) {
                server.sendToUDP(player.id, hpPacket);
            }

            // Kui HP null, eemalda järgmisel tickil updateBots sees
        }
    }


    private void setupBotUpdateLoop() {
        // Kasutab "Timer"-it, et regulaarselt uuendada bottide liikumist ja saata nende asukohad mängijatele
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (GameInstance instance : gameInstances.values()) {
                    instance.updateBots(1f);
                    for (PacketEnemyPosition packet : instance.getEnemyPositions()) {
                        for (Player player : instance.getPlayers().values()) {
                            server.sendToUDP(player.id, packet);
                        }
                    }
                }
            }
        }, 10, 10);
    }

    private void sendUpdatedPlayers(HashMap<Integer, networks.Player> players) {
        // Saadab kõigile mängijatele uuendatud info kõikidest mängijatest
        PacketUpdatePlayers packet = new PacketUpdatePlayers(players);
        for (Player player : players.values()) {
            server.sendToUDP(player.id, packet);
        }
    }

    public static void main(String[] args) {
        // Käivitab serveri
        new GameServer();
    }

    public static Server getServer() {
        return server;
    }
}