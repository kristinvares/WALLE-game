package ee.taltech.WALLE.Tools;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.WALLE.WALLEGame;

public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;


        // ground
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / WALLEGame.PPM, (rect.getY() + rect.getHeight() / 2) / WALLEGame.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rect.getWidth() / 2) / WALLEGame.PPM, (rect.getHeight() / 2) / WALLEGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
        // pipes
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / WALLEGame.PPM, (rect.getY() + rect.getHeight() / 2) / WALLEGame.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rect.getWidth() / 2) / WALLEGame.PPM, (rect.getHeight() / 2) / WALLEGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // bricks
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / WALLEGame.PPM, (rect.getY() + rect.getHeight() / 2) / WALLEGame.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rect.getWidth() / 2) / WALLEGame.PPM, (rect.getHeight() / 2) / WALLEGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // coins
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / WALLEGame.PPM, (rect.getY() + rect.getHeight() / 2) / WALLEGame.PPM);

            body = world.createBody(bdef);
            shape.setAsBox((rect.getWidth() / 2) / WALLEGame.PPM, (rect.getHeight() / 2) / WALLEGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

    }
}
