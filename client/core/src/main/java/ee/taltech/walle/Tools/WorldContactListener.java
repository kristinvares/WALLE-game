package ee.taltech.walle.Tools;

import networks.PacketBulletDestroy;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.WALLE.Sprites.Bullet;
import ee.taltech.WALLE.Sprites.PlayerSprite;
import ee.taltech.WALLE.WALLEGame;
import networks.PacketPlayerHealth;
import ee.taltech.walle.Sprites.Bullet;
import ee.taltech.walle.Sprites.PlayerSprite;
import ee.taltech.walle.walleGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldContactListener implements ContactListener {
    private final walleGame game;
    private static final Logger logger = LoggerFactory.getLogger(WorldContactListener.class);


    public WorldContactListener(walleGame game) {
        this.game = game;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Kontrollime, kas kuul tabas seina
        if ((fixA.getUserData() instanceof Bullet || fixB.getUserData() instanceof Bullet) &&
            ((fixA.getUserData() != null && fixA.getUserData().equals("WALL")) ||
                (fixB.getUserData() != null && fixB.getUserData().equals("WALL")))) {


            Bullet bullet = fixA.getUserData() instanceof Bullet ? (Bullet) fixA.getUserData() : (Bullet) fixB.getUserData();
            bullet.markForDestruction();
            if (!bullet.isRemote()) {
                PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                destroyPacket.bulletId = bullet.getId();
                game.client.sendUDP(destroyPacket);
            }
        }

        // Kontrollime, kas kuul tabas mängijat
        if ((fixA.getUserData() instanceof Bullet && fixB.getUserData() instanceof PlayerSprite) ||
            (fixB.getUserData() instanceof Bullet && fixA.getUserData() instanceof PlayerSprite)) {

            Bullet bullet = fixA.getUserData() instanceof Bullet ? (Bullet) fixA.getUserData() : (Bullet) fixB.getUserData();
            PlayerSprite player = fixA.getUserData() instanceof PlayerSprite ? (PlayerSprite) fixA.getUserData() : (PlayerSprite) fixB.getUserData();

            player.takeDamage(1);
            System.out.println("Mängija sai tabamuse! Elud: " + player.getHealth());
            if (fixA.getUserData().equals("WALL") || fixB.getUserData().equals("WALL")) {
                bullet.markForDestruction(); // ← Muudetud eemaldamiseks ohutult
                if (!bullet.isRemote()) { // Ainult lokaalse kuuli korral
                    PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                    destroyPacket.bulletId = bullet.getId();
                    destroyPacket.gameId = game.gameId;
                    game.client.sendUDP(destroyPacket);
                }

            PacketPlayerHealth healthPacket = new PacketPlayerHealth();
            healthPacket.id = game.client.getID(); // ← või `player.getId()` kui sul on meetod
            healthPacket.newHealth = player.getHealth();
            game.client.sendUDP(healthPacket);

            bullet.markForDestruction();
            if (!bullet.isRemote()) {
                PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                destroyPacket.bulletId = bullet.getId();
                game.client.sendUDP(destroyPacket);
            }
        }

        // Kontrollime, kas mängija põrkas vastu seina
        if ((fixA.getUserData() != null && fixA.getUserData().equals("WALL") && fixB.getUserData() instanceof PlayerSprite) ||
            (fixB.getUserData() != null && fixB.getUserData().equals("WALL") && fixA.getUserData() instanceof PlayerSprite)) {

            System.out.println("Mängija põrkas vastu seina!");
            // Siin saad vajadusel lisada tagasilöögi (knockback) või muud efekti
            logger.info("Mängija põrkas vastu seina!");
        }


    }

    @Override
    public void endContact(Contact contact) {
        // Pole hetkel vajalik, valmistus ette eriliste objektide jaoks
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Hetkel pole vajalik, ettevalmistus
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Hetkel pole vajalik, ettevalmistus
    }
}

