package ee.taltech.walle.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ee.taltech.walle.walleGame;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class SettingsScreen implements Screen {
    private final walleGame game;
    private final Screen previousScreen;
    private Stage stage;
    private Skin skin;

    private Slider brightnessSlider;
    private Slider volumeSlider;
    private Slider bgmSlider;
    private Slider sfxSlider;
    private Label brightnessLabel;
    private Label volumeLabel;
    private Label bgmLabel;
    private Label sfxLabel;
    private Texture arrowTexture;

    // Kaks fonti
    private BitmapFont whiteFont;
    private BitmapFont blueFont;

    private SpriteBatch batch;

    // Background
    private Texture background1;
    private Texture background2;
    private Texture background3;
    private Texture background4;

    public SettingsScreen(walleGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        arrowTexture = new Texture(Gdx.files.internal("buttons_dividers/Divider Fade/divider-fade-003.png"));

        // Fontdid
        whiteFont = new BitmapFont(Gdx.files.internal("fonts/cinzel-settings.fnt"));
        blueFont = new BitmapFont(Gdx.files.internal("fonts/cinzel.fnt"));

        // Background
        background1 = new Texture(Gdx.files.internal("menu_background/1.png"));
        background2 = new Texture(Gdx.files.internal("menu_background/2.png"));
        background3 = new Texture(Gdx.files.internal("menu_background/3.png"));
        background4 = new Texture(Gdx.files.internal("menu_background/4.png"));

        // Sliderid
        initializeSliders();

        // Back nupp
        TextButton backButton = createCustomButton("BACK", blueFont);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitSettings();
            }
        });

        // Menu layout
        setUpMenuLayout(backButton);
    }

    private void initializeSliders() {
        // Brightness slider
        brightnessSlider = new Slider(-100, 100, 1, false, skin);
        brightnessSlider.setValue(0);
        brightnessLabel = new Label("Brightness: " + (int) brightnessSlider.getValue(), new Label.LabelStyle(whiteFont, null));
        brightnessSlider.addListener(event -> {
            brightnessLabel.setText("Brightness: " + (int) brightnessSlider.getValue());
            return false;
        });

        // Volume slider
        volumeSlider = new Slider(1, 100, 1, false, skin);
        volumeSlider.setValue(50);
        volumeLabel = new Label("Master volume: " + (int) volumeSlider.getValue(), new Label.LabelStyle(whiteFont, null));
        volumeSlider.addListener(event -> {
            volumeLabel.setText("Master volume: " + (int) volumeSlider.getValue());
            return false;
        });

        // BGM slider
        bgmSlider = new Slider(1, 100, 1, false, skin);
        bgmSlider.setValue(50);
        bgmLabel = new Label("Background music: " + (int) bgmSlider.getValue(), new Label.LabelStyle(whiteFont, null));
        bgmSlider.addListener(event -> {
            bgmLabel.setText("Background music: " + (int) bgmSlider.getValue());
            return false;
        });

        // SFK slider
        sfxSlider = new Slider(1, 100, 1, false, skin);
        sfxSlider.setValue(50);
        sfxLabel = new Label("SFX / UI: " + (int) sfxSlider.getValue(), new Label.LabelStyle(whiteFont, null));
        sfxSlider.addListener(event -> {
            sfxLabel.setText("SFX / UI: " + (int) sfxSlider.getValue());
            return false;
        });
    }

    private void setUpMenuLayout(TextButton backButton) {
        // Paneb paika menuu layouti
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        float sliderWidth = 340f;

        mainTable.add(createLabeledRow(brightnessLabel)).padBottom(10);
        mainTable.row();
        mainTable.add(brightnessSlider).width(sliderWidth).padBottom(20);
        mainTable.row();

        mainTable.add(createLabeledRow(volumeLabel)).padBottom(10);
        mainTable.row();
        mainTable.add(volumeSlider).width(sliderWidth).padBottom(20);
        mainTable.row();

        mainTable.add(createLabeledRow(bgmLabel)).padBottom(10);
        mainTable.row();
        mainTable.add(bgmSlider).width(sliderWidth).padBottom(20);
        mainTable.row();

        mainTable.add(createLabeledRow(sfxLabel)).padBottom(10);
        mainTable.row();
        mainTable.add(sfxSlider).width(sliderWidth).padBottom(20);
        mainTable.row();

        mainTable.add(backButton).padTop(20);

        stage.addActor(mainTable);
    }

    private Table createLabeledRow(Label label) {
        // Teeb labelid
        Texture rightArrowTexture = new Texture(Gdx.files.internal("buttons_dividers/Divider Fade/divider-fade-003-reverse.png"));
        Image rightArrow = new Image(new TextureRegionDrawable(new TextureRegion(rightArrowTexture)));

        Image leftArrow = new Image(new TextureRegionDrawable(new TextureRegion(arrowTexture)));

        label.setAlignment(Align.center);
        label.setWrap(false);

        Table row = new Table();
        row.defaults().space(10);
        row.add(leftArrow).padRight(5);
        row.add(label).padLeft(5).padRight(5);
        row.add(rightArrow).padLeft(5);

        row.center();
        return row;
    }

    private TextButton createCustomButton(String buttonText, BitmapFont buttonFont) {
        // Teeb back return nupu
        TextureRegion buttonRegion = new TextureRegion(new Texture(Gdx.files.internal("buttons_dividers/Transparent border/panel-transparent-border-030.png")));
        NinePatch ninePatch = new NinePatch(buttonRegion, 20, 20, 8, 8);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new NinePatchDrawable(ninePatch);
        buttonStyle.down = new NinePatchDrawable(ninePatch);
        buttonStyle.font = buttonFont;

        TextButton button = new TextButton(buttonText, buttonStyle);
        return button;
    }

    private void exitSettings() {
        // Lahkumiseks
        if (previousScreen instanceof MenuScreen) {
            Gdx.input.setInputProcessor(((MenuScreen) previousScreen).getStage());
        } else if (previousScreen instanceof PauseScreen) {
            Gdx.input.setInputProcessor(((PauseScreen) previousScreen).getStage());
        }
        game.setScreen(previousScreen);
    }

    @Override
    public void show() {
        // not needed right now, for pop up menus later
    }

    @Override
    public void render(float delta) {
        // Joonistab elemendid ja scalib backgroundi
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float bgWidth = background1.getWidth();
        float bgHeight = background1.getHeight();

        float scaleX = screenWidth / bgWidth;
        float scaleY = screenHeight / bgHeight;

        float scale = Math.max(scaleX, scaleY);

        float x = (screenWidth - bgWidth * scale) / 2;
        float y = (screenHeight - bgHeight * scale) / 2;

        batch.begin();
        batch.draw(background1, x, y, bgWidth * scale, bgHeight * scale);
        batch.draw(background2, x, y, bgWidth * scale, bgHeight * scale);
        batch.draw(background3, x, y, bgWidth * scale, bgHeight * scale);
        batch.draw(background4, x, y, bgWidth * scale, bgHeight * scale);
        batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            exitSettings();
        }
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // hetkel pole kasutusel lisa featurite jaoks
    }
    @Override
    public void resume() {
        // hetkel pole kasutusel lisa featurite jaoks
    }
    @Override
    public void hide() {
        // hetkel pole kasutusel lisa featurite jaoks
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        arrowTexture.dispose();
        whiteFont.dispose();
        blueFont.dispose();
        batch.dispose();
        background1.dispose();
        background2.dispose();
        background3.dispose();
        background4.dispose();
    }
}
