package ee.taltech.walle.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ee.taltech.walle.walleGame;

public class DeathScreen implements Screen {

    private final walleGame game;
    private Stage stage;
    private BitmapFont whiteFont;
    private BitmapFont buttonFont;
    private SpriteBatch batch;

    private Texture background1, background2, background3, background4;
    private Texture buttonTexture;
    private Texture arrowTexture, arrowReversed;

    private float scaleX, scaleY, scale, x, y;

    public DeathScreen(walleGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Taasta menüümuusika helitugevus vastavalt salvestatud väärtusele
        if (game.getMenuMusic() != null && game.getMenuMusic().isPlaying()) {
            float volume = game.getPreferences().getFloat("menu_volume", 50f);
            game.getMenuMusic().setVolume(volume / 100f);
        }

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        whiteFont = new BitmapFont(Gdx.files.internal("fonts/cinzel-settings.fnt"));
        buttonFont = new BitmapFont(Gdx.files.internal("fonts/cinzel.fnt"));
        buttonTexture = new Texture(Gdx.files.internal("buttons_dividers/Transparent border/panel-transparent-border-030.png"));

        // Taustakihid
        background1 = new Texture(Gdx.files.internal("menu_background/1.png"));
        background2 = new Texture(Gdx.files.internal("menu_background/2.png"));
        background3 = new Texture(Gdx.files.internal("menu_background/3.png"));
        background4 = new Texture(Gdx.files.internal("menu_background/4.png"));

        // Nooled labeli ümber
        arrowTexture = new Texture(Gdx.files.internal("buttons_dividers/Divider Fade/divider-fade-003.png"));
        arrowReversed = new Texture(Gdx.files.internal("buttons_dividers/Divider Fade/divider-fade-003-reverse.png"));

        // Labelite stiil valge fondiga
        LabelStyle titleStyle = new LabelStyle(whiteFont, Color.WHITE);
        LabelStyle subtitleStyle = new LabelStyle(whiteFont, Color.WHITE);

        Label title = new Label("YOU DIED", titleStyle);
        title.setFontScale(1f);
        title.setAlignment(Align.center);

        Label subtitle = new Label("THAT WAS YOUR ONLY CHANCE", subtitleStyle);
        subtitle.setFontScale(1f);
        subtitle.setAlignment(Align.center);

        TextButton quitButton = createCustomButton("GIVE UP");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Paigutus
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Table textTable = new Table();
        textTable.add(createLabeledRow(title)).padBottom(20).row();
        textTable.add(subtitle).padBottom(40).row();
        textTable.add(quitButton).width(200).height(60);

        table.add(textTable);
        stage.addActor(table);
    }

    private TextButton createCustomButton(String buttonText) {
        TextureRegion buttonRegion = new TextureRegion(buttonTexture);
        NinePatch ninePatch = new NinePatch(buttonRegion, 20, 20, 8, 8);

        TextButtonStyle style = new TextButtonStyle();
        style.up = new NinePatchDrawable(ninePatch);
        style.down = new NinePatchDrawable(ninePatch);
        style.font = buttonFont;

        return new TextButton(buttonText, style);
    }

    private Table createLabeledRow(Label label) {
        Image leftArrow = new Image(new TextureRegionDrawable(new TextureRegion(arrowTexture)));
        Image rightArrow = new Image(new TextureRegionDrawable(new TextureRegion(arrowReversed)));

        Table row = new Table();
        row.defaults().space(10);
        row.add(leftArrow).padRight(5);
        row.add(label).padLeft(5).padRight(5);
        row.add(rightArrow).padLeft(5);
        return row;
    }

    @Override
    public void render(float delta) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float bgWidth = background1.getWidth();
        float bgHeight = background1.getHeight();

        scaleX = screenWidth / bgWidth;
        scaleY = screenHeight / bgHeight;
        scale = Math.max(scaleX, scaleY);

        x = (screenWidth - bgWidth * scale) / 2;
        y = (screenHeight - bgHeight * scale) / 2;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background1, x, y, bgWidth * scale, bgHeight * scale);
        batch.draw(background2, x, y, bgWidth * scale, bgHeight * scale);
        batch.draw(background3, x, y, bgWidth * scale, bgHeight * scale);
        batch.draw(background4, x, y, bgWidth * scale, bgHeight * scale);
        batch.end();

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
        batch.dispose();
        whiteFont.dispose();
        buttonFont.dispose();
        buttonTexture.dispose();
        arrowTexture.dispose();
        arrowReversed.dispose();
        background1.dispose();
        background2.dispose();
        background3.dispose();
        background4.dispose();
    }
}
