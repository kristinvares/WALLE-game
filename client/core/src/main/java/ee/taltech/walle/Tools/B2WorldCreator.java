package ee.taltech.walle.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.walle.walleGame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class B2WorldCreator {
    private Vector2 playerSpawnPosition;
    private Rectangle exitRect;
    private static final Logger logger = LoggerFactory.getLogger(B2WorldCreator.class);

    public B2WorldCreator(World world, TiledMap map) {

        for (RectangleMapObject object : map.getLayers().get("collision").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            // SPAWN – ainult salvesta positsioon
            if (object.getProperties().containsKey("spawn")) {
                playerSpawnPosition = new Vector2(rect.getX() / walleGame.PPM, rect.getY() / walleGame.PPM);
                logger.info("Spawn punkt määratud: {}", playerSpawnPosition);
                continue;
            }

            // EXIT – lisa sensorina, mitte seinana
            if (object.getProperties().containsKey("exit")) {
                exitRect = rect;
                logger.info("Exit punkt määratud: {}", exitRect);

                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX() + rect.getWidth() / 2) / walleGame.PPM,
                    (rect.getY() + rect.getHeight() / 2) / walleGame.PPM);

                Body body = world.createBody(bdef);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox((rect.getWidth() / 2) / walleGame.PPM, (rect.getHeight() / 2) / walleGame.PPM);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                fdef.isSensor = true;
                fdef.filter.categoryBits = walleGame.EXIT_BIT;
                fdef.filter.maskBits = walleGame.PLAYER_BIT;

                body.createFixture(fdef).setUserData("EXIT");
                shape.dispose();
                continue;
            }

            // SEIN – tavaline staticBody koos collision filtritega
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / walleGame.PPM,
                (rect.getY() + rect.getHeight() / 2) / walleGame.PPM);

            Body body = world.createBody(bdef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox((rect.getWidth() / 2) / walleGame.PPM, (rect.getHeight() / 2) / walleGame.PPM);

            FixtureDef fdef = new FixtureDef();
            fdef.shape = shape;
            fdef.filter.categoryBits = walleGame.WALL_BIT;
            fdef.filter.maskBits = walleGame.PLAYER_BIT | walleGame.BULLET_BIT;

            body.createFixture(fdef).setUserData("WALL");
            shape.dispose();
        }

        if (playerSpawnPosition == null) {
            logger.error("VIGA: Spawn punkt puudub kaardil!");
        }

        if (exitRect == null) {
            logger.error("VIGA: Exit punkt puudub kaardil!");
        }
    }

    public Vector2 getPlayerSpawnPosition() {
        return playerSpawnPosition;
    }

    public Rectangle getExitRect() {
        return exitRect;
    }
}
