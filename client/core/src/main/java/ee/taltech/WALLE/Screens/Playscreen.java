package ee.taltech.WALLE.Screens;


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
import ee.taltech.WALLE.Sprites.Player;
import ee.taltech.WALLE.Tools.B2WorldCreator;
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
    private Player player;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;
    private Client client;
    private Map<Integer, float[]> receivedGameObjects = new HashMap<>();
    private Map<Integer, Player> remotePlayers = new HashMap<>();


    public Playscreen(WALLEGame game, Client client) {
        this.client = client;
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gameCam = new OrthographicCamera();

        // loome FitViewpordi, et säilitada mängu laius ja kõrgus, kui ekraani suurendada/vähendada
        gamePort = new FitViewport(WALLEGame.V_WIDTH / WALLEGame.PPM, WALLEGame.V_HEIGHT / WALLEGame.PPM, gameCam);

        // hudi tegemine
        hud = new Hud(game.batch);

        // laadime maailma ja teeme mapi renderdaja
        maploader = new TmxMapLoader();
        map = maploader.load("level2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / WALLEGame.PPM);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0,-10), true);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);


        player = new Player(world, this);
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    private void sendPositionInfoToServer() {
        String positionData = String.valueOf(player.b2body.getPosition().x) + "," + String.valueOf(player.b2body.getPosition().y);
        System.out.println("SENDING POSITION TO SERVER: " + positionData);
        client.sendUDP(positionData);
    }

    @Override
    public void show() {
    }

    public void handleInput(float dt){
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
            player.b2body.applyLinearImpulse(new Vector2(2f, 0), player.b2body.getWorldCenter(), true);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
            player.b2body.applyLinearImpulse(new Vector2(-2f, 0), player.b2body.getWorldCenter(), true);
        }

    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1/60f,6, 2);
        player.update(dt);
        sendPositionInfoToServer();
        receivedGameObjects = game.getReceivedGameObjects();
        for (Map.Entry<Integer, float[]> entry : receivedGameObjects.entrySet()) {
            int id = entry.getKey();
            if (id == client.getID()) continue;

            float[] coordinates = entry.getValue();

            if (remotePlayers.containsKey(id)) {
                remotePlayers.get(id).setPosition(coordinates[0], coordinates[1]);
            } else {
                Player newPlayer = new Player(world, this);
                newPlayer.setPosition(coordinates[0], coordinates[1]);
                remotePlayers.put(id, newPlayer);
            }
        }
        gameCam.position.x = player.b2body.getPosition().x;
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
        for (Player remotePlayer : remotePlayers.values()) {
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
