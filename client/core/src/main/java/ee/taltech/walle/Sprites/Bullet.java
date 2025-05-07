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
    private boolean markedForDestruction;
    private Texture bulletTexture;
    private int bulletId;
    private boolean isRemote;
    private static final Logger logger = LoggerFactory.getLogger(Bullet.class);

    // Kohaliku kuuli konstruktor
    public Bullet(World world, Playscreen screen, float x, float y, Vector2 direction) {
        this(world, screen, x, y, direction, false); // Vaikimisi kohalik kuul
    }

    // Üldine konstruktor, saab määrata kas on remote kuul
    public Bullet(World world, Playscreen screen, float x, float y, Vector2 direction, boolean isRemote) {
        this.world = world;
        destroyed = false;
        markedForDestruction = false;
        this.isRemote = isRemote;

        // Kui graafikakeskkond on olemas, siis lae tekstuur
        if (Gdx.graphics != null) {
            bulletTexture = new Texture("bullet.png");
            setRegion(bulletTexture);
        } else {
            logger.warn("Graafikakeskkond pole valmis - tekstuuri ei loodud!");
        }

        setBounds(0, 0, 8 / walleGame.PPM, 8 / walleGame.PPM); // Kuuli suurus mängumaailmas

        defineBullet(x, y, direction.nor()); // Loome Box2D keha
    }

    // Kuuli Box2D füüsilise keha loomine ja liikumissuuna määramine
    public void defineBullet(float x, float y, Vector2 direction) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(5 / walleGame.PPM); // Väike raadius, et kuul oleks pisike

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = walleGame.BULLET_BIT;
        fdef.filter.maskBits = walleGame.WALL_BIT | walleGame.PLAYER_BIT | walleGame.ENEMY_BIT; // Mille vastu kuul põrkub

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();

        b2body.setLinearVelocity(direction.scl(2.5f)); // Liikumiskiirus
    }

    public void setId(int id) {
        this.bulletId = id; // Määrab kuuli ID (vajalik võrgumängus)
    }

    public int getId() {
        return bulletId;
    }

    // Kuuli uuendamine - liikumine ja vajadusel hävitamine
    public void update(float dt) {
        if (!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2);

            // Kui märgitud hävitamiseks, hävita Box2D keha
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
        return destroyed; // Märgi kuul hävitamiseks järgmisel kaadril
    }

    public void markForDestruction() {
        markedForDestruction = true;
    }

    public void dispose() {
        bulletTexture.dispose();
    }

    public boolean isRemote() {
        return isRemote;
    }

    // Kasulik juhul kui kuuli asukoht on valesti paigas (nt võrgu kaudu loodud)
    public void correctPosition(float newX, float newY) {
        b2body.setTransform(newX, newY, b2body.getAngle());
    }
}

