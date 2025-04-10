package ee.taltech.walle.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.walle.Screens.Playscreen;
import ee.taltech.walle.walleGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bullet extends Sprite {
    public World world;
    public Body b2body;
    private boolean destroyed;
    private boolean markedForDestruction; // ← LISATUD!
    // private float speed = 6f; when we might wanna change it after
    private Texture bulletTexture;
    private int bulletId;
    private boolean isRemote;
    private static final Logger logger = LoggerFactory.getLogger(Bullet.class);


    public Bullet(World world, Playscreen screen, float x, float y, Vector2 direction) {
        this(world, screen, x, y, direction, false); // <-- Vaikimisi kohalik kuul
    }

    public Bullet(World world, Playscreen screen, float x, float y, Vector2 direction, boolean isRemote) {  // igaks juhuks jatan
        this.world = world;
        destroyed = false;
        markedForDestruction = false;
        this.isRemote = isRemote;

        if (Gdx.graphics != null) {
            bulletTexture = new Texture("bullet.png");
            setRegion(bulletTexture);
        } else {
            logger.warn("Graafikakeskkond pole valmis - tekstuuri ei loodud!");
        }

        setBounds(0, 0, 8 / walleGame.PPM, 8 / walleGame.PPM);

        defineBullet(x, y, direction.nor());
    }

    public void defineBullet(float x, float y, Vector2 direction) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(5 / walleGame.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = walleGame.BULLET_BIT;
        fdef.filter.maskBits = walleGame.WALL_BIT | walleGame.PLAYER_BIT;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();

        b2body.setLinearVelocity(direction.scl(2.5f));
    }

    public void setId(int id) {  // <-- Lisatud setId meetod
        this.bulletId = id;
    }

    public int getId() {  // <-- Lisatud getId meetod
        return bulletId;
    }

    public void update(float dt) {  // antakse edasi uhte teisse filei ei vota ara igaks juhuks
        if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2);

            if (markedForDestruction && !destroyed) {
                Gdx.app.postRunnable(() -> {
                    if (!destroyed) {
                        world.destroyBody(b2body);
                        destroyed = true;
                    }
                });
            }
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void markForDestruction() {
        markedForDestruction = true;  // ← LISATUD!
    }

    public void dispose() {
        bulletTexture.dispose();
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void correctPosition(float newX, float newY) {
        b2body.setTransform(newX, newY, b2body.getAngle());
    }
}

