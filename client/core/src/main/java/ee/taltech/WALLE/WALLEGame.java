package ee.taltech.WALLE;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import com.esotericsoftware.kryonet.Client;
import java.io.IOException;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class WALLEGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private int x = 0, y = 0;

    private Client client;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        client = new Client();
        client.start();
        client.sendTCP("Start");
        try {
            client.connect(5000, "localhost", 8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            x -= 10;
            sendPositionInfoToServer();
        }
        batch.begin();
        //batch.draw(image, 140, 210); <- Janne koodirida :)
        // Agol oli videos nii. Tegin samamoodi
        batch.draw(image, x, y);
        batch.end();
        client.sendUDP("test");
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
