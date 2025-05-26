package ee.taltech.walle.Screens;

import networks.PacketIsMultiPlayer;
import networks.PacketIsSinglePlayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.walle.Tools.TiledMapLoader;
import ee.taltech.walle.walleGame;
import com.badlogic.gdx.audio.Music;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class MenuScreen implements Screen {
    private final walleGame game;
    private static final String TAG_MAP_GEN = "MapGen"; // Logimise jaoks konstandiks defineeritud silt
    private Stage stage;
    private Client client;
    private BitmapFont font;
    private Texture buttonTexture;

    private Texture background1;
    private Texture background2;
    private Texture background3;
    private Texture background4;
    private float scaleX;
    private float scaleY;
    private float scale;
    private float x;
    private float y;

    // muusika sound
    private Music menuMusic;

    // Peamenüü nupud
    private TextButton playButton, multiplayerButton, settingsButton, exitButton;

    public MenuScreen(walleGame game, Client client) {
        this.game = game;
        this.client = client;

        // Lava ja sisendi seadistamine
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Font ja nupu tekstuur
        font = new BitmapFont(Gdx.files.internal("fonts/cinzel.fnt"));
        buttonTexture = new Texture(Gdx.files.internal("buttons_dividers/Transparent border/panel-transparent-border-030.png"));

        // Menüü taustad
        background1 = new Texture(Gdx.files.internal("menu_background/1.png"));
        background2 = new Texture(Gdx.files.internal("menu_background/2.png"));
        background3 = new Texture(Gdx.files.internal("menu_background/3.png"));
        background4 = new Texture(Gdx.files.internal("menu_background/4.png"));

        // Nuppude loomine ja kuulajate lisamine
        playButton = createCustomButton("PLAY");
        multiplayerButton = createCustomButton("MULTIPLAYER");
        settingsButton = createCustomButton("SETTINGS");
        exitButton = createCustomButton("QUIT");

        // Üksikmängu nupp
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                int[][] map = generateCollisionMap(); // Collision-kaardi loomine
                client.sendTCP(new PacketIsSinglePlayer(client.getID(), map)); // Saada serverile
                Gdx.app.log("Menu", "📨 SP kaardi info saadetud serverile");
                game.setScreen(game.getPlayscreen()); // Ava mäng
            }
        });

        // Mitmikmängu nupp
        multiplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                int[][] map = generateCollisionMap();
                client.sendTCP(new PacketIsMultiPlayer(client.getID(), map));
                game.setScreen(game.getPlayscreen());
            }
        });

        // Seadete nupp
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, MenuScreen.this));
            }
        });

        // Välju nupust
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Nuppude paigutus tabelisse
        Table table = new Table();
        table.setFillParent(true);
        table.center();
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
        // Kohandatud nupu loomine tekstuuri ja fondiga
        TextureRegion buttonRegion = new TextureRegion(buttonTexture);
        NinePatch ninePatch = new NinePatch(buttonRegion, 20, 20, 8, 8);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new NinePatchDrawable(ninePatch);
        buttonStyle.down = new NinePatchDrawable(ninePatch);
        buttonStyle.font = font;

        return new TextButton(buttonText, buttonStyle);
    }

    private int[][] generateCollisionMap() {
        // Collision-kaardi genereerimine 'collision' layeri põhjal
        Gdx.app.log(TAG_MAP_GEN, "📦 Alustan collision-kaardi genereerimist...");
        TiledMapLoader loader = new TiledMapLoader("map.tmx");
        TiledMap map = loader.getMap();

        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        int[][] collisionMap = new int[width][height];

        MapLayer collisionLayer = map.getLayers().get("collision");
        if (collisionLayer == null) {
            Gdx.app.error(TAG_MAP_GEN, "⛔ Kaardil puudub 'collision' layer!");
            return collisionMap;
        }

        // Tuvasta kõik ristkülikud ja märgi need kollisioonina
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject rectObj) {
                Rectangle rect = rectObj.getRectangle();
                int startX = (int) (rect.getX() / tileWidth);
                int startY = (int) (rect.getY() / tileHeight);
                int endX = (int) ((rect.getX() + rect.getWidth()) / tileWidth);
                int endY = (int) ((rect.getY() + rect.getHeight()) / tileHeight);

                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            collisionMap[x][y] = 1;
                        }
                    }
                }
            }
        }

        Gdx.app.log(TAG_MAP_GEN, "✅ Collision-map genereeritud suurusega " + width + "x" + height);
        return collisionMap;
    }

    @Override public void show() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sound_menu/Map_music.ogg"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f);
        menuMusic.play();
    }
    @Override public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float bgWidth = background1.getWidth();
        float bgHeight = background1.getHeight();

        scaleX = screenWidth / bgWidth;
        scaleY = screenHeight / bgHeight;
        scale = Math.max(scaleX, scaleY);

        x = (screenWidth - bgWidth * scale) / 2;
        y = (screenHeight - bgHeight * scale) / 2;

        game.batch.begin();
        game.batch.draw(background1, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background2, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background3, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.draw(background4, x, y, bgWidth * scale, bgHeight * scale);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {
        // Hetkel pole vajalik, potensiaalseste menuude jaoks
    }
    @Override public void resume() {
        // Hetkel pole vajalik, potensiaalseste menuude jaoks
    }
    @Override public void hide() {
        // Hetkel pole vajalik, potensiaalseste menuude jaoks
    }

    @Override public void dispose() {
        stage.dispose();
        font.dispose();
        buttonTexture.dispose();
        background1.dispose();
        background2.dispose();
        background3.dispose();
        background4.dispose();

        // muusika puhastamine
        if (menuMusic != null) {
            menuMusic.dispose();
        }
    }

    public Stage getStage() {
        return stage;
    }
}
