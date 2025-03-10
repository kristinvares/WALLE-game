package ee.taltech.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof String) {
                    System.out.println("RECEIVED POSITION FROM CLIENT " + connection.getID() + ": " + object);
                    gameObjects.put(connection.getID(), (String) object);
                }

                System.out.println("SENDING UPDATED POSITIONS TO CLIENTS: " + gameObjects);
                server.sendToAllUDP(gameObjects);
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("CLIENT DISCONNECTED: " + connection.getID());
                gameObjects.remove(connection.getID());
            }
        });
    }

    public static void main(String[] args) {
        new GameServer();
    }
}
