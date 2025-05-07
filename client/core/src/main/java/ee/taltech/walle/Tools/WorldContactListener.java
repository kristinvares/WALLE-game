package ee.taltech.walle.Tools;

import networks.PacketBulletDestroy;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.walle.Sprites.Bullet;
import ee.taltech.walle.Sprites.PlayerSprite;
import ee.taltech.walle.walleGame;
import networks.PacketPlayerHealth;

import ee.taltech.walle.Sprites.EnemySprite;
import networks.PacketEnemyHit;

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

        handleBulletHitsWall(fixA, fixB);
        handleBulletHitsPlayer(fixA, fixB);
        handleBulletHitsEnemy(fixA, fixB);
        handlePlayerHitsWall(fixA, fixB);
    }

    private void handleBulletHitsWall(Fixture fixA, Fixture fixB) {
        if ((fixA.getUserData() instanceof Bullet || fixB.getUserData() instanceof Bullet) &&
            ("WALL".equals(fixA.getUserData()) || "WALL".equals(fixB.getUserData()))) {

            Bullet bullet = fixA.getUserData() instanceof Bullet ? (Bullet) fixA.getUserData() : (Bullet) fixB.getUserData();
            bullet.markForDestruction();

            if (!bullet.isRemote()) {
                PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                destroyPacket.bulletId = bullet.getId();
                game.client.sendUDP(destroyPacket);
            }
        }
    }

    private void handleBulletHitsPlayer(Fixture fixA, Fixture fixB) {
        if ((fixA.getUserData() instanceof Bullet && fixB.getUserData() instanceof PlayerSprite) ||
            (fixB.getUserData() instanceof Bullet && fixA.getUserData() instanceof PlayerSprite)) {

            Bullet bullet = fixA.getUserData() instanceof Bullet ? (Bullet) fixA.getUserData() : (Bullet) fixB.getUserData();
            PlayerSprite player = fixA.getUserData() instanceof PlayerSprite ? (PlayerSprite) fixA.getUserData() : (PlayerSprite) fixB.getUserData();

            player.takeDamage(1);
            logger.info("Mängija sai tabamuse! Elud: {}", player.getHealth());

            PacketPlayerHealth healthPacket = new PacketPlayerHealth();
            healthPacket.id = game.client.getID();
            healthPacket.newHealth = player.getHealth();
            game.client.sendUDP(healthPacket);

            bullet.markForDestruction();

            if (!bullet.isRemote()) {
                PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                destroyPacket.bulletId = bullet.getId();
                destroyPacket.gameId = game.gameId;
                game.client.sendUDP(destroyPacket);
            }
        }
    }

    private void handleBulletHitsEnemy(Fixture fixA, Fixture fixB) {
        if ((fixA.getUserData() instanceof Bullet && fixB.getUserData() instanceof EnemySprite) ||
            (fixB.getUserData() instanceof Bullet && fixA.getUserData() instanceof EnemySprite)) {

            Bullet bullet = fixA.getUserData() instanceof Bullet ? (Bullet) fixA.getUserData() : (Bullet) fixB.getUserData();
            EnemySprite enemy = fixA.getUserData() instanceof EnemySprite ? (EnemySprite) fixA.getUserData() : (EnemySprite) fixB.getUserData();

            // Lõpeta kuuli elutsükkel
            bullet.markForDestruction();

            if (!bullet.isRemote()) {
                PacketBulletDestroy destroyPacket = new PacketBulletDestroy();
                destroyPacket.bulletId = bullet.getId();
                destroyPacket.gameId = game.gameId;
                game.client.sendUDP(destroyPacket);
            }

            // Saada serverisse hit info
            PacketEnemyHit hitPacket = new PacketEnemyHit();
            hitPacket.enemyId = enemy.getId();  // Peab olema olemas getter EnemySprite sees
            hitPacket.gameId = game.gameId;
            game.client.sendUDP(hitPacket);

            logger.info("🎯 Kuul tabas boti! ID: {}", enemy.getId());
        }
    }

    private void handlePlayerHitsWall(Fixture fixA, Fixture fixB) {
        if (("WALL".equals(fixA.getUserData()) && fixB.getUserData() instanceof PlayerSprite) ||
            ("WALL".equals(fixB.getUserData()) && fixA.getUserData() instanceof PlayerSprite)) {
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
