package ee.taltech.WALLE.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.WALLE.WALLEGame;

public class B2WorldCreator {
    private Vector2 playerSpawnPosition;

    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Collision layer - seinad
        for (MapObject object : map.getLayers().get("collision").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / WALLEGame.PPM,
                (rect.getY() + rect.getHeight() / 2) / WALLEGame.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rect.getWidth() / 2) / WALLEGame.PPM, (rect.getHeight() / 2) / WALLEGame.PPM);
            fdef.shape = shape;
            fdef.isSensor = false; // Ei lase mängijal neist läbi minna
            body.createFixture(fdef);
        }

        // Mängija `spawn` punkt
        for (MapObject object : map.getLayers().get("collision").getObjects()) {
            if (object.getProperties().containsKey("spawn")) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                playerSpawnPosition = new Vector2(rect.getX() / WALLEGame.PPM, rect.getY() / WALLEGame.PPM);
                System.out.println("✅ Spawn punkt määratud: " + playerSpawnPosition);
                return;
            }
        }

        if (playerSpawnPosition == null) {
            System.err.println("❗️ VIGA: Spawn punkt puudub kaardil!");
        }
    }

    public Vector2 getPlayerSpawnPosition() {
        return playerSpawnPosition;
    }
}


