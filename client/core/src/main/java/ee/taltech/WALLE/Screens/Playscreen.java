package ee.taltech.WALLE.Screens;

import Network.PacketPosition;
import Network.PacketUpdatePlayers;
import Network.Player;
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
import ee.taltech.WALLE.WALLEGame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public Playscreen(WALLEGame game, Client client) {
        this.client = client;
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gameCam = new OrthographicCamera();

        gamePort = new FitViewport(WALLEGame.V_WIDTH / WALLEGame.PPM, WALLEGame.V_HEIGHT / WALLEGame.PPM, gameCam);

        hud = new Hud(game.batch);

        tiledMapLoader = new TiledMapLoader("map.tmx");
        map = tiledMapLoader.getMap();

        renderer = tiledMapLoader.setupMap();
        renderer.setMap(map);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true);
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
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement.x = Math.min(velocity.x + acceleration, moveSpeed);
        } else {
            movement.x *= 0.9f;
            if (Math.abs(movement.x) < 0.1f) movement.x = 0;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movement.y = Math.min(velocity.y + acceleration, moveSpeed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movement.y = Math.max(velocity.y - acceleration, -moveSpeed);
        } else {
            movement.y *= 0.9f;
            if (Math.abs(movement.y) < 0.1f) movement.y = 0;
        }

        player.b2body.setLinearVelocity(movement);
        player.b2body.setLinearDamping(2.5f);

        float maxSpeed = 1.5f;
        if (player.b2body.getLinearVelocity().len() > maxSpeed) {
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().nor().scl(maxSpeed));
        }
    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);

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

        tiledMapLoader.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}


