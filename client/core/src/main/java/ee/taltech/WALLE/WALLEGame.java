package ee.taltech.WALLE;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
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

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class WALLEGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private Texture opponentImg;
    private int x = 0, y = 0;
    private Map<Integer, SpriteBatch> opponentBatches = new HashMap<>();
    private Map<Integer, String> receivedGameObjects = new HashMap<>();

    private Client client;
    private int clientId;

    @Override
    public void create() {

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        opponentImg = new Texture("drop.jpg");

        client = new Client();
        client.start();
        Kryo kryo = client.getKryo();
        kryo.register(HashMap.class);
        try {
            client.connect(5000, "localhost", 8080, 8081);
            clientId = client.getID();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                System.out.print("received: " + object);
                if (object instanceof Map) {
                    receivedGameObjects = (Map<Integer, String>) object;
                }
            }
        }));

    }

    private void sendPositionInfoToServer() {
        client.sendUDP(x + "," + y);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            x -= 10;
            sendPositionInfoToServer();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            x += 10;
            sendPositionInfoToServer();
        }
        batch.begin();
        //batch.draw(image, 140, 210); <- Janne koodirida :)
        // Agol oli videos nii. Tegin samamoodi
        batch.draw(image, x, y);
        batch.end();
        for (Map.Entry<Integer, String> entry : receivedGameObjects.entrySet()) {
            int id = entry.getKey();
            if (id == clientId) continue;

            if (!opponentBatches.containsKey(id)) {
                opponentBatches.put(id, new SpriteBatch());
            }

            SpriteBatch b = opponentBatches.get(id);
            String[] coordinates = entry.getValue().split(",");
            b.begin();
            b.draw(opponentImg, Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
            b.end();
        }

        if (receivedGameObjects != null) {
            receivedGameObjects.clear();
        }
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
        image.dispose();
    }
}
