package ee.taltech.WALLE.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.WALLE.WALLEGame;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class MenuScreen implements Screen {
    private final WALLEGame game;
    private Stage stage;
    private Client client;

    public MenuScreen(WALLEGame game, Client client) {
        this.game = game;
        this.client = client;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create buttons with custom borders and text
        TextButton playButton = createCustomButton("Play");
        TextButton multiplayerButton = createCustomButton("Multiplayer");
        TextButton settingsButton = createCustomButton("Settings");
        TextButton exitButton = createCustomButton("Quit");

        // Add button listeners
        addButtonListeners(playButton, multiplayerButton, settingsButton, exitButton);

        // Add buttons to the stage
        Table table = new Table();
        table.top().setFillParent(true);
        table.add(playButton).fillX().uniformX().pad(10);
        table.row().pad(10, 0, 10, 0);
        table.add(multiplayerButton).fillX().uniformX().pad(10);
        table.row().pad(10, 0, 10, 0);
        table.add(settingsButton).fillX().uniformX().pad(10);
        table.row().pad(10, 0, 10, 0);
        table.add(exitButton).fillX().uniformX().pad(10);

        stage.addActor(table);
    }

    private TextButton createCustomButton(String buttonText) {
        // Load the custom border images (make sure the paths are correct)
        Texture buttonTexture = new Texture(Gdx.files.internal("Transparent_border/panel-transparent-border-001.png"));
        TextureRegion buttonRegion = new TextureRegion(buttonTexture);

        // Create NinePatch from the loaded texture (for border/stretching)
        NinePatch ninePatch = new NinePatch(buttonRegion, 4, 4, 4, 4); // Adjust the padding values as needed

        // Create the TextButtonStyle and set the background textures for the button
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(buttonRegion); // Set normal state
        buttonStyle.down = new TextureRegionDrawable(buttonRegion); // Set pressed state (optional)
        buttonStyle.font = new BitmapFont();  // Use default font for text

        // Create the TextButton with the defined style
        TextButton button = new TextButton(buttonText, buttonStyle);

        // Add button click listener
        button.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println(buttonText + " button clicked!");
                // Handle screen transitions or actions based on the button clicked
                switch (buttonText) {
                    case "Play":
                        game.setScreen(new Playscreen(game, client)); // Play screen transition
                        break;
                    // case "Multiplayer":
                    // game.setScreen(new MultiplayerScreen(game));  // Multiplayer screen transition
                    // break;
                    case "Settings":
                        game.setScreen(new SettingsScreen(game, MenuScreen.this));  // Pass MenuScreen as the previous screen
                        break;
                    case "Quit":
                        Gdx.app.exit();  // Quit the application
                        break;
                }
            }
        });

        return button;
    }

    private void addButtonListeners(TextButton playButton, TextButton multiplayerButton, TextButton settingsButton, TextButton exitButton) {
        // Note: Button listeners have been integrated into the `createCustomButton` method already
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {
        // This is where you set up any additional logic that happens when the screen is shown.
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);

        stage.act(delta);  // Update the stage (perform actions on the actors)
        stage.draw();  // Render the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // You can save any game state here if needed.
    }

    @Override
    public void resume() {
        // This is called when the screen is resumed after being paused.
    }

    @Override
    public void hide() {
        // This is called when the screen is hidden.
    }

    @Override
    public void dispose() {
        stage.dispose();  // Dispose of the stage when done
    }
}
