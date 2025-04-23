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

    private Texture background1;
    private Texture background2;
    private Texture background3;
    private Texture background4;

    private float scaleX;
    private float scaleY;
    private float scale;

    private float x;
    private float y;


    public PauseScreen(walleGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont(Gdx.files.internal("fonts/cinzel.fnt"));
        buttonTexture = new Texture(Gdx.files.internal("buttons_dividers/Transparent border/panel-transparent-border-030.png"));

        // Lae background
        background1 = new Texture(Gdx.files.internal("menu_background/1.png"));
        background2 = new Texture(Gdx.files.internal("menu_background/2.png"));
        background3 = new Texture(Gdx.files.internal("menu_background/3.png"));
        background4 = new Texture(Gdx.files.internal("menu_background/4.png"));

        // Tee nupud
        TextButton resumeButton = createCustomButton("RESUME");
        TextButton settingsButton = createCustomButton("SETTINGS");
        TextButton mainMenuButton = createCustomButton("MAIN MENU");
        TextButton exitButton = createCustomButton("EXIT");

        // Lisa listenerid nuppudele
        addButtonListeners(resumeButton, settingsButton, mainMenuButton, exitButton);

        // Loo ja pane tabel keskele
        Table table = new Table();
        table.setFillParent(true);
        table.center();

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
        // Tee NinePatch nupu tekstuurist (hoia 8px bordereid)
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
                game.setScreen(previousScreen); // Mine eelmisele ekraanile
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, PauseScreen.this)); // Mine settingutesse
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game, null)); // Mine main menuusse
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();  // Lahku mangust
            }
        });
    }

    @Override
    public void show() {
        // Tuleviku menuu featureite jaoks
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // saa ekraani suurus
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // saa background suurus
        float bgWidth = background1.getWidth();
        float bgHeight = background1.getHeight();

        // scalei
        scaleX = screenWidth / bgWidth;
        scaleY = screenHeight / bgHeight;

        scale = Math.max(scaleX, scaleY);

        // centerimiseks vaja
        x = (screenWidth - bgWidth * scale) / 2;
        y = (screenHeight - bgHeight * scale) / 2;

        // joonista background
        game.batch.begin();
        game.batch.draw(background1, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background2, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background3, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background4, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.end();

        // joonista up elemendid)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Tuleviku menuu featureite jaoks
    }

    @Override
    public void resume() {
        // Tuleviku menuu featureite jaoks
    }

    @Override
    public void hide() {
        // Tuleviku menuu featureite jaoks
    }

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
