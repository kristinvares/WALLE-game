package ee.taltech.game.server;

import networks.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {
    private Server server;
    private HashMap<Integer, Player> players = new HashMap<>();
    private HashMap<Integer, BulletData> activeBullets = new HashMap<>();
    private Queue<Integer> availableIds = new LinkedList<>();
    private AtomicInteger nextBulletId = new AtomicInteger(1); // Unikaalsed ID-d
    private HashMap<Integer, Integer> tempToRealBulletMap = new HashMap<>();

    public GameServer() {
        server = new Server();
        server.start();

        Kryo kryo = server.getKryo();
        kryo.register(PacketPosition.class);
        kryo.register(Player.class);
        kryo.register(PacketUpdatePlayers.class);
        kryo.register(HashMap.class);
        kryo.register(BulletData.class);
        kryo.register(PacketBulletDestroy.class);
        kryo.register(PacketDisconnect.class);
        kryo.register(PacketPlayerHealth.class);


        try {
            server.bind(8080, 8081);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("CLIENT CONNECTED: " + connection.getID());

                Player newPlayer = new Player(connection.getID(), 0, 0, "Player_" + connection.getID());
                players.put(connection.getID(), newPlayer);

                sendUpdatedPlayers();
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketPosition packet) {
                    if (players.containsKey(packet.id)) {
                        Player player = players.get(packet.id);
                        player.x = packet.x;
                        player.y = packet.y;

                        sendUpdatedPlayers();
                    }
                }
                if (object instanceof BulletData packet) {
                    int realBulletId = nextBulletId.getAndIncrement(); // Uus ID serveris
                    tempToRealBulletMap.put(packet.bulletId, realBulletId); // Seome ajutise ja tõelise ID
                    packet.bulletId = realBulletId;  // Määra uus ametlik ID

                    activeBullets.put(realBulletId, new BulletData(packet.x, packet.y, packet.shooterID));

                    System.out.println("➡️ SERVER SAADAB KUULI ID-ga: " + packet.bulletId +
                            " | POSITSIOON: " + packet.x + ", " + packet.y);
                    server.sendToAllUDP(packet);
                }

                if (object instanceof PacketBulletDestroy packet) {
                    activeBullets.remove(packet.bulletId);
                    availableIds.add(packet.bulletId);
                    server.sendToAllUDP(packet);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("CLIENT DISCONNECTED: " + connection.getID());
                players.remove(connection.getID());
                sendUpdatedPlayers();
            }
        });
    }

    private void sendUpdatedPlayers() {
        PacketUpdatePlayers packet = new PacketUpdatePlayers(players);
        server.sendToAllUDP(packet);
    }

    public static void main(String[] args) {
        new GameServer();
    }
}

