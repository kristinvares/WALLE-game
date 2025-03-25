package ee.taltech.WALLE.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ee.taltech.WALLE.WALLEGame;

public class SettingsScreen implements Screen {
    private final WALLEGame game;
    private final Screen previousScreen; // Mäletab eelmist ekraani
    private Stage stage;
    private Skin skin;
    private Slider brightnessSlider, volumeSlider;
    private Label brightnessLabel, volumeLabel;

    public SettingsScreen(WALLEGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // hetkel pole seda faili

        // === HELEDUS SLIDER ===
        brightnessSlider = new Slider(1, 100, 1, false, skin);
        brightnessSlider.setValue(50); // algväärtus, testimiseks
        brightnessLabel = new Label("Brightness: " + (int) brightnessSlider.getValue(), skin);

        brightnessSlider.addListener(event -> {
            brightnessLabel.setText("Brightness: " + (int) brightnessSlider.getValue());
            return false;
        });  // funktsionaalsus praegu puudub

        // === HELITUUGEVISE SLIDER ===
        volumeSlider = new Slider(1, 100, 1, false, skin);
        volumeSlider.setValue(50); // algväärtus, testimiseks
        volumeLabel = new Label("Volume: " + (int) volumeSlider.getValue(), skin);

        volumeSlider.addListener(event -> {
            volumeLabel.setText("Volume: " + (int) volumeSlider.getValue());
            return false;
        });

        // === BACK BUTTON ===
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                exitSettings();
            }
        });

        // === SETTINGS MENÜÜ ===
        Table table = new Table();
        table.setFillParent(true);
        table.add(brightnessLabel).padBottom(10);
        table.row();
        table.add(brightnessSlider).width(300).padBottom(20);
        table.row();
        table.add(volumeLabel).padBottom(10);
        table.row();
        table.add(volumeSlider).width(300).padBottom(20);
        table.row();
        table.add(backButton).padTop(20);

        stage.addActor(table);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // kui vajutada ESC, minnakse tagasi eelmisele ekraanile
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            exitSettings();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private void exitSettings() {
        if (previousScreen instanceof MenuScreen) {
            Gdx.input.setInputProcessor(((MenuScreen) previousScreen).getStage());
        } else if (previousScreen instanceof PauseScreen) {
            Gdx.input.setInputProcessor(((PauseScreen) previousScreen).getStage());
        }
        game.setScreen(previousScreen);
    }


    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
