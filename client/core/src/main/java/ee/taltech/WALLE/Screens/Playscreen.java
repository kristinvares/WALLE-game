package ee.taltech.WALLE.Screens;

import network.*;
import network.BulletData;

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
import ee.taltech.WALLE.Scenes.Hud;
import ee.taltech.WALLE.Sprites.PlayerSprite;
import ee.taltech.WALLE.Tools.B2WorldCreator;
import ee.taltech.WALLE.Tools.TiledMapLoader;
import ee.taltech.WALLE.Tools.WorldContactListener;
import ee.taltech.WALLE.WALLEGame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.utils.Array; // LISATUD KUULIDE HALDAMISEKS
import ee.taltech.WALLE.Sprites.Bullet; // LISATUD KUULI KLASS

public class Playscreen implements Screen {
    private WALLEGame game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private PlayerSprite player;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;
    private Client client;
    private Map<Integer, PlayerSprite> remotePlayers = new HashMap<>();
    private TiledMapLoader tiledMapLoader;
    Vector2 spawnPosition;
    private Array<Bullet> bullets;
    private HashMap<Integer, Bullet> remoteBullets = new HashMap<>();
    private int tempBulletId = 1000;


    public Playscreen(WALLEGame game, Client client) {
        this.client = client;
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        bullets = new Array<>();
        gameCam = new OrthographicCamera();

        gamePort = new FitViewport(WALLEGame.V_WIDTH / WALLEGame.PPM, WALLEGame.V_HEIGHT / WALLEGame.PPM, gameCam);

        hud = new Hud(game.batch);

        tiledMapLoader = new TiledMapLoader("map.tmx");
        map = tiledMapLoader.getMap();

        renderer = tiledMapLoader.setupMap();
        renderer.setMap(map);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new WorldContactListener(game));
        b2dr = new Box2DDebugRenderer();

        B2WorldCreator worldCreator = new B2WorldCreator(world, map);
        spawnPosition = worldCreator.getPlayerSpawnPosition();


        player = new PlayerSprite(world, this, spawnPosition.x, spawnPosition.y);

    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    // === Saada mängija asukoht serverile ===
    private void sendPositionInfoToServer() {
        PacketPosition packet = new PacketPosition();
        packet.id = client.getID();
        packet.x = player.b2body.getPosition().x;
        packet.y = player.b2body.getPosition().y;

        client.sendUDP(packet);
    }

    public void createRemoteBullet(BulletData packet) {
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

                System.out.println("✅ REMOTE KUUL LISATUD ID-ga: " + packet.bulletId);
            }
        });
    }

    public void removeRemoteBullet(int bulletId) {
        Gdx.app.postRunnable(() -> {
            if (remoteBullets.containsKey(bulletId)) {
                Bullet bullet = remoteBullets.get(bulletId);
                bullet.markForDestruction();
                remoteBullets.remove(bulletId);
                System.out.println(remoteBullets.size());
            }
        });
    }

    @Override
    public void show() {}

    // === Mängija sisendi haldamine ===
    public void handleInput(float dt) {
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

        // KUULI LASKMINE
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            Vector2 bulletDirection = new Vector2(
                (float) Math.cos(Math.toRadians(player.getRotation())),
                (float) Math.sin(Math.toRadians(player.getRotation()))
            ).nor();
            int assignedId = tempBulletId++;  // ← Ajutine ID
            Bullet bullet = new Bullet(world, this,
                player.b2body.getPosition().x,
                player.b2body.getPosition().y,
                bulletDirection);
            bullet.setId(assignedId); // ← Määra ajutine ID
            bullets.add(bullet);

            // Saada serverisse
            BulletData packet = new BulletData();
            packet.bulletId = assignedId; // Ajutine ID
            packet.x = player.b2body.getPosition().x;
            packet.y = player.b2body.getPosition().y;
            packet.directionX = bulletDirection.x;
            packet.directionY = bulletDirection.y;
            packet.shooterID = client.getID();
            System.out.println("➡️ KLIENT SAADAB KUULI ID-ga: " + packet.bulletId +
                " | POSITSIOON: " + packet.x + ", " + packet.y);

            client.sendUDP(packet);
        }
    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);
        world.setContactListener(new WorldContactListener(game));

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

        player.update(dt);
        sendPositionInfoToServer();

        HashMap<Integer, Player> players = game.getPlayers();
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

        renderer.render();
        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

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

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

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
}


