package ee.taltech.walle.Sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import ee.taltech.walle.Screens.Playscreen;
import ee.taltech.walle.walleGame;

public class EnemySprite extends Sprite {
    public Body b2body;
    private TextureRegion botTexture;

    public EnemySprite(World world, Playscreen screen, float x, float y, int id) {
        super(screen.getAtlas().findRegion("little_mario")); // sama region kui mängijal
        defineEnemy(world, x, y);
        botTexture = new TextureRegion(getTexture(), 0, 8, 16, 16);
        setBounds(0, 0, 16 / walleGame.PPM, 16 / walleGame.PPM);
        setRegion(botTexture);
    }

    private void defineEnemy(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.type = BodyDef.BodyType.StaticBody; // või DynamicBody kui tahad liikumist
        b2body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / walleGame.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = walleGame.ENEMY_BIT;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public void update(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
        if (b2body != null) {
            b2body.setTransform(x, y, b2body.getAngle());
        }
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}


