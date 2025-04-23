package ee.taltech.game.server;

import networks.BulletData;
import networks.PacketEnemyPosition;
import networks.Player;

import java.util.HashMap;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameInstance {
    private static final Logger logger = LoggerFactory.getLogger(GameInstance.class);

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
        bots.put(bot.getId(), bot); // Lisa uus bot
    }

    public Collection<Bot> getBots() {
        return bots.values(); // Tagasta kõik botid
    }

    public AtomicInteger getBotIdCounter() {
        return botIdCounter; // Botide ID loendur (kasutatakse uue ID andmiseks)
    }

    public void updateBots(float dt) {
        // Uuenda kõiki botte
        for (Bot bot : bots.values()) {
            bot.update(dt);
        }
    }

    public List<PacketEnemyPosition> getEnemyPositions() {
        // Tagastab kõikide bottide asukohad, et neid saata mängijatele
        List<PacketEnemyPosition> packets = new ArrayList<>();
        for (Bot bot : bots.values()) {
            packets.add(new PacketEnemyPosition(bot.getId(), bot.getX(), bot.getY(), gameId));
        }
        return packets;
    }

    public void addPlayer(Player player) {
        players.put(player.id, player); // Lisa mängija
    }

    public void removePlayer(int playerId) {
        players.remove(playerId); // Eemalda mängija
    }

    public void addBullet(BulletData bullet) {
        bullets.put(bullet.bulletId, bullet); // Lisa uus kuul
    }

    public void removeBullet(int bulletId) {
        bullets.remove(bulletId); // Eemalda kuul
    }

    public HashMap<Integer, Player> getPlayers() {
        return players; // Tagasta mängijate nimekiri
    }

    public HashMap<Integer, BulletData> getBullets() {
        return bullets; // Tagasta kuulide nimekiri
    }

    // Getter & Setter collision-kaardile
    public void setCollisionMap(int[][] map) {
        this.collisionMap = map; // Sea selle mängu kaardi collision-info
    }

    public int[][] getCollisionMap() {
        return collisionMap; // Saa collision-kaart
    }

    public int getGameId() {
        return gameId; // Saa instantsi ID
    }

    public void spawnBotIfNeeded() {
        // Loo bot ainult kui kaardil on collisionMap ja bottide nimekiri on tühi
        if (collisionMap != null && bots.isEmpty()) {
            Bot bot = new Bot(this, 5, 3); // Loo bot positsioonil (5,3) testimiseks moeldud spawn positsioon
            bot.setId(botIdCounter.getAndIncrement()); // Määra unikaalne ID
            addBot(bot); // Lisa bot
            logger.info("🤖 [GameInstance] Bot loodud instantsi {} jaoks", gameId);
        }
    }
}
