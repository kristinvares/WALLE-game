package ee.taltech.walle.Sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import ee.taltech.walle.Screens.Playscreen;
import ee.taltech.walle.walleGame;

public class PlayerSprite extends Sprite {
    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    private float rotationAngle;

    public PlayerSprite(World world, Playscreen screen, float startX, float startY) {
        super(screen.getAtlas().findRegion("little_mario"));
        definePlayer(world, startX, startY);
        playerStand = new TextureRegion(getTexture(),0, 8, 16, 16);
        setBounds(0,0, 16 / walleGame.PPM, 16 / walleGame.PPM);
        setRegion(playerStand);
    }

    public void update() {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }
    public void definePlayer(World world, float startX, float startY) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX, startY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / walleGame.PPM);

        fdef.shape = shape;

        // Lisa bitmaskid kokkupõrke jaoks
        fdef.filter.categoryBits = walleGame.PLAYER_BIT;
        fdef.filter.maskBits = walleGame.WALL_BIT | walleGame.ENEMY_BIT;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    @Override
    public void setRotation(float angle) {
        this.rotationAngle = angle;
    }

    @Override
    public float getRotation() {
        return rotationAngle;
    }
}
