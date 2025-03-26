package ee.taltech.game.server;

import Network.PacketPosition;
import Network.PacketUpdatePlayers;
import Network.Player;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;

// Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

    private Server server;
    private HashMap<Integer, Player> players = new HashMap<>();

    public GameServer() {
        server = new Server();
        server.start();

        Kryo kryo = server.getKryo();
        kryo.register(PacketPosition.class);
        kryo.register(Player.class);
        kryo.register(PacketUpdatePlayers.class);
        kryo.register(HashMap.class);

        try {
            // Kontrolli ühendust serveri protidega
            server.bind(8081, 8081);
            logger.info("Server started on port 8081");
        } catch (IOException e) {
            logger.error("Failed to bind server ports.", e);
        }

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                // Käsitle kliendi connectimist
                logger.info("CLIENT CONNECTED: {}", connection.getID());

                Player newPlayer = new Player(connection.getID(), 0, 0, "Player_" + connection.getID());
                players.put(connection.getID(), newPlayer);

                sendUpdatedPlayers();
            }

            @Override
            public void received(Connection connection, Object object) {
                // Updatei playeri kordinaate
                if (object instanceof PacketPosition packet && players.containsKey(packet.id)) {
                    Player player = players.get(packet.id);
                    player.x = packet.x;
                    player.y = packet.y;

                    sendUpdatedPlayers();
                }
            }

            @Override
            public void disconnected(Connection connection) {
                // Käsitle disconnecte
                logger.info("CLIENT DISCONNECTED: {}", connection.getID());
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

