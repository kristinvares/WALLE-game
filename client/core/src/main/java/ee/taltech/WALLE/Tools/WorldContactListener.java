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

        // Kontrollime, kas kuul tabas seina
        if ((fixA.getUserData() instanceof Bullet || fixB.getUserData() instanceof Bullet) &&
            (fixA.getUserData() != null && fixA.getUserData().equals("WALL") ||
                fixB.getUserData() != null && fixB.getUserData().equals("WALL"))) {

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
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
