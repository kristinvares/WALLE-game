package ee.taltech.walle.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ee.taltech.walle.walleGame;

public class PauseScreen implements Screen {
    private final walleGame game;
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture;
    private final Screen previousScreen;

    // Declare the background textures
    private Texture background1, background2, background3, background4;
    private float scaleX, scaleY, scale;
    private float x, y;

    public PauseScreen(walleGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
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
        TextButton resumeButton = createCustomButton("RESUME");
        TextButton settingsButton = createCustomButton("SETTINGS");
        TextButton mainMenuButton = createCustomButton("MAIN MENU");
        TextButton exitButton = createCustomButton("EXIT");

        // Add listeners to buttons
        addButtonListeners(resumeButton, settingsButton, mainMenuButton, exitButton);

        // Create and center the table
        Table table = new Table();
        table.setFillParent(true);
        table.center(); // Center table on screen

        table.add(resumeButton).fillX().uniformX().pad(10);
        table.row();
        table.add(settingsButton).fillX().uniformX().pad(10);
        table.row();
        table.add(mainMenuButton).fillX().uniformX().pad(10);
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

        return button;
    }

    private void addButtonListeners(TextButton resumeButton, TextButton settingsButton, TextButton mainMenuButton, TextButton exitButton) {
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(previousScreen); // Return to the previous screen (game)
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, PauseScreen.this)); // Settings screen
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game, null)); // Go to main menu
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();  // Exit the game
            }
        });
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

    public Stage getStage() {
        return stage;
    }
}
