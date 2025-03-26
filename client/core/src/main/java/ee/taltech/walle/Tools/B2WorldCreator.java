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
    private static final Logger logger = LoggerFactory.getLogger(B2WorldCreator.class);
    private Vector2 playerSpawnPosition;

    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Collision layer - seinad
        for (RectangleMapObject object : map.getLayers().get("collision").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / walleGame.PPM,
                (rect.getY() + rect.getHeight() / 2) / walleGame.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rect.getWidth() / 2) / walleGame.PPM, (rect.getHeight() / 2) / walleGame.PPM);
            fdef.shape = shape;
            fdef.isSensor = false; // Ei lase mängijal neist läbi minna
            body.createFixture(fdef);
        }

        // Mängija `spawn` punkt
        for (MapObject object : map.getLayers().get("collision").getObjects()) {
            if (object.getProperties().containsKey("spawn")) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                playerSpawnPosition = new Vector2(rect.getX() / walleGame.PPM, rect.getY() / walleGame.PPM);
                logger.info("Spawn punkt määratud: {}", playerSpawnPosition);
                return;
            }
        }

        if (playerSpawnPosition == null) {
            logger.error("VIGA: Spawn punkt puudub kaardil!");
        }
    }

    public Vector2 getPlayerSpawnPosition() {
        return playerSpawnPosition;
    }
}


