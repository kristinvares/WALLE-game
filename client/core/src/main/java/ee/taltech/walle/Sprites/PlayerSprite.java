package ee.taltech.walle.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import ee.taltech.walle.Screens.Playscreen;
import ee.taltech.walle.walleGame;

// Kristin lisas juurde
import com.badlogic.gdx.utils.Timer;

public class PlayerSprite extends Sprite {
    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    private float rotationAngle;

    private Texture witchTexture;
    private TextureRegion witchRegion;

    // health jaoks
    private int health;
    private int maxHealth;

    public PlayerSprite(World world, Playscreen screen, float startX, float startY) {
        witchTexture = new Texture("B_witch_idle.png");

        // 32 x 42 tukk sprite jaoks
        witchRegion = new TextureRegion(witchTexture, 0, 0, 32, 42);

        // Määra sprite'il visuaalsed mõõtmed ja pildiallikas
        setBounds(0, 0, 32 / walleGame.PPM, 32 / walleGame.PPM);
        setRegion(witchRegion);

        definePlayer(world, startX, startY);

        maxHealth = 100;
        health = maxHealth;
    }

    // Updatei positsiooni
    public void update() {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }

    // Defineeri playeri omadused
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
        fdef.filter.maskBits = walleGame.WALL_BIT | walleGame.BULLET_BIT | walleGame.EXIT_BIT;

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

    // elude jaoks
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
        flashRedOnHit();
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void flashRedOnHit() {
        setColor(1, 0, 0, 1); // muudab sprite punaseks
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                setColor(1, 1, 1, 1); // muutub tagasi normaalseks
            }
        }, 0.2f); // 0.2 sekundi pärast
    }
}
