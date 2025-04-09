package ee.taltech.WALLE.Screens;

import Network.PacketIsMultiPlayer;
import Network.PacketIsSinglePlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.WALLE.WALLEGame;

public class MenuScreen implements Screen {
    private final WALLEGame game;
    private Stage stage;
    private Client client;
    private BitmapFont font;
    private Texture buttonTexture;

    // Declare the background textures
    private Texture background1, background2, background3, background4;
    private float scaleX, scaleY, scale;
    private float x, y;

    public MenuScreen(WALLEGame game, Client client) {
        this.game = game;
        this.client = client;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont(Gdx.files.internal("fonts/cinzel.fnt"));
        buttonTexture = new Texture(Gdx.files.internal("buttons_dividers/Transparent border/panel-transparent-border-030.png"));

        // Load background images
        background1 = new Texture(Gdx.files.internal("menu_background/1.png"));
        background2 = new Texture(Gdx.files.internal("menu_background/2.png"));
        background3 = new Texture(Gdx.files.internal("menu_background/3.png"));
        background4 = new Texture(Gdx.files.internal("menu_background/4.png"));

        // Create buttons
        TextButton playButton = createCustomButton("PLAY");
        TextButton multiplayerButton = createCustomButton("MULTIPLAYER");
        TextButton settingsButton = createCustomButton("SETTINGS");
        TextButton exitButton = createCustomButton("QUIT");
        TextButton playButton = new TextButton("Play", skin);
        TextButton multiplayerButton = new TextButton("Multiplayer", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Button listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                client.sendTCP(new PacketIsSinglePlayer(client.getID()));
                game.setScreen(game.getPlayscreen());

                // single-player screen
            }
        });

        multiplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                client.sendTCP(new PacketIsMultiPlayer(client.getID()));
                game.setScreen(game.getPlayscreen());
            }
        });

        // Create and center the table
        Table table = new Table();
        table.setFillParent(true);
        table.center(); // Center table on screen

        table.add(playButton).fillX().uniformX().pad(10);
        table.row();
        table.add(multiplayerButton).fillX().uniformX().pad(10);
        table.row();
        table.add(settingsButton).fillX().uniformX().pad(10);
        table.row();
        table.add(exitButton).fillX().uniformX().pad(10);

        stage.addActor(table);
    }

    private TextButton createCustomButton(String buttonText) {
        // Create a NinePatch from the button texture (preserve 8px borders)
        TextureRegion buttonRegion = new TextureRegion(buttonTexture);
        NinePatch ninePatch = new NinePatch(buttonRegion, 20, 20, 8, 8);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new NinePatchDrawable(ninePatch);
        buttonStyle.down = new NinePatchDrawable(ninePatch);
        buttonStyle.font = font;

        TextButton button = new TextButton(buttonText, buttonStyle);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println(buttonText + " button clicked!");
                switch (buttonText) {
                    case "PLAY":
                        game.setScreen(new Playscreen(game, client));
                        break;
                    case "SETTINGS":
                        game.setScreen(new SettingsScreen(game, MenuScreen.this));
                        break;
                    case "QUIT":
                        Gdx.app.exit();
                        break;
                }
            }
        });

        return button;
    }

    private void addButtonListeners(TextButton... buttons) {
        // Already integrated in createCustomButton
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // Get screen dimensions
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Get background image dimensions
        float bgWidth = background1.getWidth();
        float bgHeight = background1.getHeight();

        // Calculate the scale factors for the background
        scaleX = screenWidth / bgWidth;
        scaleY = screenHeight / bgHeight;

        // Choose the smaller scale factor to avoid distortion (letterboxing or pillarboxing)
        scale = Math.max(scaleX, scaleY);

        // Calculate the position of the background to center it
        x = (screenWidth - bgWidth * scale) / 2;
        y = (screenHeight - bgHeight * scale) / 2;

        // Draw the background images, scaling and positioning them appropriately
        game.batch.begin();
        game.batch.draw(background1, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background2, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background3, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background4, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.end();

        // Draw the UI elements (buttons, sliders, labels)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        font.dispose();
        buttonTexture.dispose();
        background1.dispose();
        background2.dispose();
        background3.dispose();
        background4.dispose();
    }
}
