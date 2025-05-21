package ee.taltech.walle.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.walle.walleGame;

// Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class B2WorldCreator {
    private Vector2 playerSpawnPosition;
    private Rectangle exitRect;
    private static final Logger logger = LoggerFactory.getLogger(B2WorldCreator.class);

    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for (RectangleMapObject object : map.getLayers().get("collision").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            // Kui objekt on spawn või exit, ÄRA loo sellest kehad
            if (object.getProperties().containsKey("spawn")) {
                playerSpawnPosition = new Vector2(rect.getX() / walleGame.PPM, rect.getY() / walleGame.PPM);
                logger.info("Spawn punkt määratud: {}", playerSpawnPosition);
                continue;
            } else if (object.getProperties().containsKey("exit")) {
                exitRect = rect;
                logger.info("Exit punkt määratud: {}", exitRect);

                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX() + rect.getWidth() / 2) / walleGame.PPM,
                    (rect.getY() + rect.getHeight() / 2) / walleGame.PPM);

                body = world.createBody(bdef);
                shape.setAsBox((rect.getWidth() / 2) / walleGame.PPM, (rect.getHeight() / 2) / walleGame.PPM);
                fdef.shape = shape;
                fdef.isSensor = true;  // ← EI BLOKEERI, ainult annab märku
                body.createFixture(fdef).setUserData("EXIT");
                continue;
            }

            // Seinad
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / walleGame.PPM,
                (rect.getY() + rect.getHeight() / 2) / walleGame.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rect.getWidth() / 2) / walleGame.PPM, (rect.getHeight() / 2) / walleGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = walleGame.WALL_BIT;
            fdef.filter.maskBits = walleGame.PLAYER_BIT | walleGame.BULLET_BIT;

            body.createFixture(fdef).setUserData("WALL");
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




