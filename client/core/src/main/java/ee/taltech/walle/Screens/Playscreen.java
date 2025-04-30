package ee.taltech.walle.Screens;

import networks.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.walle.Scenes.Hud;
import ee.taltech.walle.Sprites.PlayerSprite;
import ee.taltech.walle.Tools.B2WorldCreator;
import ee.taltech.walle.Tools.TiledMapLoader;
import ee.taltech.walle.Tools.WorldContactListener;
import ee.taltech.walle.walleGame;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

// map to server
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// vastased
import ee.taltech.walle.Sprites.EnemySprite;

import com.badlogic.gdx.utils.Array; // LISATUD KUULIDE HALDAMISEKS
import ee.taltech.walle.Sprites.Bullet; // LISATUD KUULI KLASS

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Playscreen implements Screen {
    // Põhiobjektid mängu kuvamiseks
    private walleGame game;
    private TextureAtlas atlas;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private PlayerSprite player;

    // Kaardi ja Box2D maailmaga seotud objektid
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private World world;
    private Box2DDebugRenderer b2dr;

    // Võrgumängu komponendid
    private Client client;
    private Map<Integer, PlayerSprite> remotePlayers = new HashMap<>();
    private TiledMapLoader tiledMapLoader;
    private Vector2 spawnPosition;

    // Kuulide haldus
    private Array<Bullet> bullets;
    private HashMap<Integer, Bullet> remoteBullets = new HashMap<>();
    private int tempBulletId = 1000;

    // Vastaste haldus
    private HashMap<Integer, EnemySprite> enemies = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(Playscreen.class);

    public Playscreen(walleGame game, Client client) {
        // Kaardi ja maailma initsialiseerimine
        this.client = client;
        this.game = game;
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        bullets = new Array<>();

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(walleGame.V_WIDTH / walleGame.PPM, walleGame.V_HEIGHT / walleGame.PPM, gameCam);
        hud = new Hud(game.batch);

        tiledMapLoader = new TiledMapLoader("map.tmx");
        map = tiledMapLoader.getMap();
        renderer = tiledMapLoader.setupMap();
        renderer.setMap(map);

        // Kaamera algpositsioon
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // Box2D maailm ja selle kontaktide kuulaja
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        world.setContactListener(new WorldContactListener(game));

        // Mängija spawni määramine
        B2WorldCreator worldCreator = new B2WorldCreator(world, map);
        spawnPosition = worldCreator.getPlayerSpawnPosition();
        player = new PlayerSprite(world, this, spawnPosition.x, spawnPosition.y);

        // Collision-kaardi saatmine serverile
        sendMapToServer();
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    // Saada mängija asukoht serverile
    private void sendPositionInfoToServer() {
        PacketPosition packet = new PacketPosition();
        packet.id = client.getID();
        packet.x = player.b2body.getPosition().x;
        packet.y = player.b2body.getPosition().y;
        packet.gameId = game.gameId;
        client.sendUDP(packet);
    }

    public void createRemoteBullet(BulletData packet) {
        // Kontrollib, kas kuul on teiselt mängijalt, ja lisab selle
        if (packet.shooterID == client.getID()) {
            return;
        }

        // Remote kuul (teiste mängijate kuulid)
        Gdx.app.postRunnable(() -> {
            if (!remoteBullets.containsKey(packet.bulletId)) {
                Vector2 direction = new Vector2(packet.directionX, packet.directionY);
                Bullet bullet = new Bullet(world, this, packet.x, packet.y, direction, true);
                bullet.setId(packet.bulletId);
                remoteBullets.put(packet.bulletId, bullet);

                logger.info("✅ REMOTE KUUL LISATUD ID-ga: {}", packet.bulletId);
            }
        });
    }

    public void removeRemoteBullet(int bulletId) {
        // Eemaldab kuuli kui see on serverist kustutatud
        Gdx.app.postRunnable(() -> {
            if (remoteBullets.containsKey(bulletId)) {
                Bullet bullet = remoteBullets.get(bulletId);
                bullet.markForDestruction();
                remoteBullets.remove(bulletId);
                logger.info("remoteBullets size: {}", remoteBullets.size());
            }
        });
    }

    @Override
    public void show() {
        // hetkel pole vajalik, pop up menüüde jaoks ette valmistus
    }

    // Mängija sisendi haldamine
    public void handleInput() {
        float moveSpeed = 3.5f;
        float acceleration = 0.5f;

        Vector2 velocity = player.b2body.getLinearVelocity();
        Vector2 movement = new Vector2(velocity.x, velocity.y);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement.x = Math.max(velocity.x - acceleration, -moveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement.x = Math.min(velocity.x + acceleration, moveSpeed);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movement.y = Math.min(velocity.y + acceleration, moveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movement.y = Math.max(velocity.y - acceleration, -moveSpeed);
        }

        // Kui liikumist on toimunud, määrame pöördenurga
        if (movement.len() > 0.1f) {
            float angle = (float) Math.toDegrees(Math.atan2(movement.y, movement.x));
            player.setRotation(angle);
        }

        player.b2body.setLinearVelocity(movement);
        player.b2body.setLinearDamping(2.5f);

        float maxSpeed = 1.5f;
        if (player.b2body.getLinearVelocity().len() > maxSpeed) {
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().nor().scl(maxSpeed));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
        }

        // Kuuli laskmine
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            Vector2 bulletDirection = new Vector2(
                (float) Math.cos(Math.toRadians(player.getRotation())),
                (float) Math.sin(Math.toRadians(player.getRotation()))
            ).nor();
            int assignedId = tempBulletId++;  //  Ajutine ID
            Bullet bullet = new Bullet(world, this,
                player.b2body.getPosition().x,
                player.b2body.getPosition().y,
                bulletDirection);
            bullet.setId(assignedId); //  Määra ajutine ID
            bullets.add(bullet);

            // Saada serverisse
            BulletData packet = new BulletData();
            packet.bulletId = assignedId; // Ajutine ID
            packet.x = player.b2body.getPosition().x;
            packet.y = player.b2body.getPosition().y;
            packet.directionX = bulletDirection.x;
            packet.directionY = bulletDirection.y;
            packet.shooterID = client.getID();
            packet.gameID = game.gameId;
            logger.info("➡️ KLIENT SAADAB KUULI ID-ga: {} | POSITSIOON: {}, {}", packet.bulletId, packet.x, packet.y);

            client.sendUDP(packet);
        }
    }

    private void sendMapToServer() {  // Funktsionaalsuse sailitamiseks palun ajutiselt ara tee luhemaks
        // Collision-layeri andmed saadetakse serverisse
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        int[][] collisionMap = new int[width][height];

        // Collision layer
        MapLayer collisionLayer = map.getLayers().get("collision");
        if (collisionLayer == null) {
            logger.error("Kaardil puudub 'collision' layer!");
            return;
        }

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
    }


    public void update(float dt) {
        // Uuendab mänguloogikat
        handleInput();
        world.step(1 / 60f, 6, 2);
        world.setContactListener(new WorldContactListener(game));

        // Kuulide elutsükli haldamine
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            if (bullet.getX() == 0 && bullet.getY() == 0) {
                bullet.correctPosition(player.b2body.getPosition().x, player.b2body.getPosition().y);
            }
            bullet.update(dt);
            if (bullet.isDestroyed()) {
                PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                destroyPacket.bulletId = bullet.getId();
                destroyPacket.gameId = game.gameId;
                client.sendUDP(destroyPacket);
                iter.remove();
            }
        }
        Iterator<Bullet> iterRemote = remoteBullets.values().iterator();
        while (iterRemote.hasNext()) {
            Bullet bullet = iterRemote.next();
            bullet.update(dt);
            if (bullet.isDestroyed()) {
                iterRemote.remove();

            }
        }

        // Mängijate sünkroniseerimine
        player.update();
        sendPositionInfoToServer();

        Map<Integer, Player> players = game.getPlayers();
        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
            int id = entry.getKey();
            Player data = entry.getValue();

            if (id == client.getID()) continue;

            if (remotePlayers.containsKey(id)) {
                PlayerSprite remotePlayer = remotePlayers.get(id);
                remotePlayer.b2body.setTransform(new Vector2(data.x, data.y), remotePlayer.b2body.getAngle());
                remotePlayer.setPosition(remotePlayer.b2body.getPosition().x - remotePlayer.getWidth() / 2,
                    remotePlayer.b2body.getPosition().y - remotePlayer.getHeight() / 2);
            } else {
                PlayerSprite newPlayer = new PlayerSprite(world, this, spawnPosition.x, spawnPosition.y);
                newPlayer.b2body.setTransform(new Vector2(data.x, data.y), newPlayer.b2body.getAngle());
                remotePlayers.put(id, newPlayer);
            }
        }

        // Kaamera liikumine mängijaga kaasa
        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;
        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float dt) {
        update(dt);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Mängukaardi ja objektide joonistamine
        renderer.render();
        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        for (EnemySprite enemy : enemies.values()) {
            enemy.draw(game.batch);
        }

        for (PlayerSprite remotePlayer : remotePlayers.values()) {
            remotePlayer.draw(game.batch);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(game.batch);
        }

        for (Bullet bullet : remoteBullets.values()) {
            bullet.draw(game.batch);
        }

        player.draw(game.batch);

        game.batch.end();

        // HUD joonistamine
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Bullet bullet : bullets) {
            bullet.dispose();
        }

        tiledMapLoader.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public void updateEnemyPosition(PacketEnemyPosition packet) {
        Gdx.app.postRunnable(() -> {
            EnemySprite enemy = enemies.get(packet.id);
            if (enemy == null) {
                float x = packet.x * 16 / walleGame.PPM;
                float y = packet.y * 16 / walleGame.PPM;
                enemy = new EnemySprite(world, this, x, y, packet.id);
                enemy.update(packet.x, packet.y);
                enemies.put(packet.id, enemy);
                logger.info("🎯 BOT loodud ID-ga {} positsioonil ({}, {})", packet.id, packet.x, packet.y);
            } else {
                enemy.update(packet.x, packet.y);
                logger.info("Bot update: id={}, pos=({}, {})", packet.id, packet.x, packet.y);
            }
        });
    }
}




