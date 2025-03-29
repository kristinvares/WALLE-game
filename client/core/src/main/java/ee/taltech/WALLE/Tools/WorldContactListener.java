package ee.taltech.WALLE.Tools;

import Network.PacketBulletDestroy;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.WALLE.Sprites.Bullet;
import ee.taltech.WALLE.Sprites.PlayerSprite;
import ee.taltech.WALLE.WALLEGame;

public class WorldContactListener implements ContactListener {
    private final WALLEGame game;

    public WorldContactListener(WALLEGame game) {
        this.game = game;
    }
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Kontrollime, kas kokkupõrge on kuuliga
        if (fixA.getUserData() instanceof Bullet || fixB.getUserData() instanceof Bullet) {
            Bullet bullet = fixA.getUserData() instanceof Bullet ?
                (Bullet) fixA.getUserData() :
                (Bullet) fixB.getUserData();

            if (fixA.getUserData().equals("WALL") || fixB.getUserData().equals("WALL")) {
                bullet.markForDestruction(); // ← Muudetud eemaldamiseks ohutult
                if (!bullet.isRemote()) { // Ainult lokaalse kuuli korral
                    PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                    destroyPacket.bulletId = bullet.getId();
                    game.client.sendUDP(destroyPacket);
                }

            }
        }
        // Kontrollime, kas kokkupõrge on mängijaga ja seinaga
        if ((fixA.getUserData() != null && fixA.getUserData().equals("WALL") && fixB.getUserData() instanceof PlayerSprite) ||
            (fixB.getUserData() != null && fixB.getUserData().equals("WALL") && fixA.getUserData() instanceof PlayerSprite)) {

            // Siin saad vajadusel lisada tagasilöögi (knockback) või muud efekti
            System.out.println("Mängija põrkas vastu seina!");
        }


    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}

