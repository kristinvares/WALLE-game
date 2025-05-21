package ee.taltech.walle.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ee.taltech.walle.walleGame;

public class EndScreen implements Screen {

    private final walleGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public EndScreen(walleGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // Võid muuta vastavalt oma resolutsioonile
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Võid kasutada oma .fnt faili
    }

    @Override
    public void show() {
        // Ei midagi hetkel
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.getData().setScale(2f);
        font.draw(batch, "Mäng läbi!", 300, 300);
        font.getData().setScale(1f);
        font.draw(batch, "Vajuta SPACE, et alustada uuesti.", 240, 250);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}

