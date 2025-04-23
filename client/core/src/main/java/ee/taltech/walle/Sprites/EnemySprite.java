package ee.taltech.walle.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import ee.taltech.walle.Screens.Playscreen;
import ee.taltech.walle.walleGame;


public class EnemySprite extends Sprite {
    public Body b2body;
    private Texture slimeTexture;
    private TextureRegion slimeRegion;

    // Teeb spritei
    public EnemySprite(World world, Playscreen screen, float x, float y, int id) {
        slimeTexture = new Texture("Slime_idle.png");
        slimeRegion = new TextureRegion(slimeTexture, 0, 0, 32, 32); // võtab vasak-ülalt 16x16 piksli sprite'i

        setRegion(slimeRegion);
        setBounds(0, 0, 16 / walleGame.PPM, 16 / walleGame.PPM);

        defineEnemy(world, x, y);
    }

    // Hitbox
    private void defineEnemy(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(6 / walleGame.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = walleGame.ENEMY_BIT;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    // Updatei positsiooni
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

    public void dispose() {
        if (slimeTexture != null) {
            slimeTexture.dispose();
        }
    }
}
