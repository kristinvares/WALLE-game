package ee.taltech.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GameServer {
    private Server server;
    private Map<Integer, String> gameObjects = new HashMap<>();

    public GameServer() {
        server = new Server();
        server.start();
        Kryo kryo = server.getKryo();
        kryo.register(HashMap.class);

        try {
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.addListener(new Listener() {
            // received meetod mis pannakse kaima kui serverisse tuleb pakett
            // connection info uhenduse kohta
            // object mida saadeti
            public void received (Connection connection, Object object) {
                System.out.println(object);
                if (object instanceof String) {
                    gameObjects.put(connection.getID(), (String) object);
                }
                server.sendToAllUDP(gameObjects);
            }
            @Override
            public void disconnected(Connection connection) {
                gameObjects.remove(connection.getID());
            }
        });
    }
    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
    }
}
