package ee.taltech.WALLE.Sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import ee.taltech.WALLE.Screens.Playscreen;
import ee.taltech.WALLE.WALLEGame;

public class PlayerSprite extends Sprite {
    public World world;
    public Body b2body;
    private TextureRegion playerStand;

    public PlayerSprite(World world, Playscreen screen, float startX, float startY) {
        super(screen.getAtlas().findRegion("little_mario"));
        definePlayer(world, startX, startY);
        playerStand = new TextureRegion(getTexture(),0, 8, 16, 16);
        setBounds(0,0, 16 / WALLEGame.PPM, 16 / WALLEGame.PPM);
        setRegion(playerStand);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }
    public void definePlayer(World world, float startX, float startY) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX, startY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / WALLEGame.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }
}
