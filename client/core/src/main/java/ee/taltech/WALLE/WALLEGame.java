package ee.taltech.WALLE;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.WALLE.Screens.Playscreen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WALLEGame extends Game {
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;

    public SpriteBatch batch;
    private Client client;
    private int clientId;


    private Map<Integer, float[]> receivedGameObjects = new HashMap<>();

    @Override
    public void create () {
        batch = new SpriteBatch();
        client = new Client();
        client.start();
        Kryo kryo = client.getKryo();
        kryo.register(HashMap.class);

        try {
            client.connect(5000, "localhost", 8080, 8081);
            clientId = client.getID();
            System.out.println("CONNECTED TO SERVER");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.addListener(new Listener.ThreadedListener(new Listener() {

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Map) {
                    Map<Integer, String> gameObjects = (Map<Integer, String>) object;
                    System.out.println("CLIENT RECEIVED DATA: " + gameObjects);

                    for (Map.Entry<Integer, String> entry : gameObjects.entrySet()) {
                        int id = entry.getKey();
                        String[] coordinates = entry.getValue().split(",");

                        if (id == clientId) continue; // Ignoreeri enda koordinaate

                        try {
                            float posX = Float.parseFloat(coordinates[0].trim());
                            float posY = Float.parseFloat(coordinates[1].trim());
                            receivedGameObjects.put(id, new float[]{posX, posY});
                        } catch (NumberFormatException e) {
                            System.err.println("ERROR: Invalid coordinates received: " + entry.getValue());
                        }
                    }
                }
            }
        }));

        setScreen(new Playscreen(this, client));
    }

    public Map<Integer, float[]> getReceivedGameObjects() {
        return receivedGameObjects;
    }
    @Override
    public void render () {
        super.render();
    }
}


