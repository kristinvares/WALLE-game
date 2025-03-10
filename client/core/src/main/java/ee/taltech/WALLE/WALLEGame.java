package ee.taltech.WALLE;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WALLEGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture playerTexture;
    private Texture opponentTexture;

    private int x = 0, y = 0; // Mängija koordinaadid
    private Map<Integer, int[]> receivedGameObjects = new HashMap<>();

    private Client client;
    private int clientId;

    @Override
    public void create() {
        batch = new SpriteBatch();
        playerTexture = new Texture("libgdx.png");
        opponentTexture = new Texture("drop.jpg");

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
                            int posX = Integer.parseInt(coordinates[0].trim());
                            int posY = Integer.parseInt(coordinates[1].trim());
                            receivedGameObjects.put(id, new int[]{posX, posY});
                        } catch (NumberFormatException e) {
                            System.err.println("ERROR: Invalid coordinates received: " + entry.getValue());
                        }
                    }
                }
            }
        }));
    }

    private void sendPositionInfoToServer() {
        String positionData = x + "," + y;
        System.out.println("SENDING POSITION TO SERVER: " + positionData);
        client.sendUDP(positionData);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= 10;
            sendPositionInfoToServer();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += 10;
            sendPositionInfoToServer();
        }

        // Joonista mängija
        batch.begin();
        batch.draw(playerTexture, x, y);

        // Joonista teised mängijad
        for (Map.Entry<Integer, int[]> entry : receivedGameObjects.entrySet()) {
            int id = entry.getKey();
            System.out.println("Minu id on " + clientId);
            System.out.println("saadud id on " + id);

            if (id == clientId) {
                continue;
            }

            int[] coordinates = entry.getValue();
            batch.draw(opponentTexture, coordinates[0], coordinates[1]);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        batch.dispose();
        playerTexture.dispose();
        opponentTexture.dispose();
    }
}


