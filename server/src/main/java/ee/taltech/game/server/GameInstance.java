package ee.taltech.game.server;

import networks.BulletData;
import networks.PacketEnemyPosition;
import networks.Player;

import java.util.HashMap;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.ArrayList;
import java.util.List;

public class GameInstance {
    private int gameId;
    private HashMap<Integer, Player> players;
    private HashMap<Integer, BulletData> bullets;

    // Bots
    private HashMap<Integer, Bot> bots = new HashMap<>();
    private AtomicInteger botIdCounter = new AtomicInteger(-1);

    // Collision kaart
    private int[][] collisionMap;

    public GameInstance(int gameId) {
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.bullets = new HashMap<>();
    }

    public void addBot(Bot bot) {
        bots.put(bot.getId(), bot);
    }

    public Collection<Bot> getBots() {
        return bots.values();
    }

    public AtomicInteger getBotIdCounter() {
        return botIdCounter;
    }

    public void updateBots(float dt) {
        for (Bot bot : bots.values()) {
            bot.update(dt);
        }
    }

    public List<PacketEnemyPosition> getEnemyPositions() {
        List<PacketEnemyPosition> packets = new ArrayList<>();
        for (Bot bot : bots.values()) {
            packets.add(new PacketEnemyPosition(bot.getId(), bot.getX(), bot.getY(), gameId));
        }
        return packets;
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

    // Getter & Setter collision-kaardile
    public void setCollisionMap(int[][] map) {
        this.collisionMap = map;
    }

    public int[][] getCollisionMap() {
        return collisionMap;
    }

    public int getGameId() {
        return gameId;
    }
}
