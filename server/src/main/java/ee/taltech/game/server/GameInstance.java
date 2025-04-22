package ee.taltech.game.server;

import networks.BulletData;
import networks.Player;

import java.util.HashMap;

public class GameInstance {
    private int gameId; // it isnt unused
    private HashMap<Integer, Player> players;
    private HashMap<Integer, BulletData> bullets;

    public GameInstance(int gameId) {
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.bullets = new HashMap<>();
    }

    public void addPlayer(Player player) {
        players.put(player.id, player);
    }

    public void removePlayer(int playerId) {
        players.remove(playerId);
    }

    public void addBullet(BulletData bullet) {
        bullets.put(bullet.bulletId, bullet);
    }

    public void removeBullet(int bulletId) {
        bullets.remove(bulletId);
    }

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }
    public HashMap<Integer, BulletData> getBullets() {
        return bullets;
    }
}
