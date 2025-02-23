package ee.taltech.game.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class GameServer {
    private Server server;

    public GameServer() {
        server = new Server();
        server.start();
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
            }
        });
    }
    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
    }
}
