package ee.taltech.game.server;

import networks.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.util.List;

public class Bot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private float x;
    private float y;
    private int id;
    private GameInstance gameInstance; // Mängu instants, milles bot eksisteerib
    private List<Point> path; // Arvutatud tee mängijani
    private int pathIndex = 0; // Järgmine punkt teel, mille poole bot liigub
    private float pathTimer = 0; // Aeg mõõtmaks, millal tee uuesti arvutada

    private int health = 100;
    private int maxHealth = 100;

    public Bot(GameInstance gameInstance, float x, float y) {
        this.gameInstance = gameInstance;
        this.x = x;
        this.y = y;
    }

    // Kutsutakse iga kaadri jooksul, et uuendada boti loogikat
    public void update(float dt) {
        // Kontrolli, kas mängus on mängijaid
        if (gameInstance.getPlayers().isEmpty()) {
            logger.warn("⚠️ [BOT {}] Mängijaid ei ole.", id);
            return;
        }

        // Kontrolli, kas collision map on olemas
        if (gameInstance.getCollisionMap() == null) {
            logger.warn("⚠️ [BOT {}] Collision map puudub!", id);
            return;
        }

        // Leia lähim mängija sihtmärgiks
        Player target = getClosestPlayer();
        if (target == null) return;

        // Arvuta uus tee iga 0.5 sekundi tagant või kui tee on tühi või otsas
        pathTimer += dt;
        if (pathTimer >= 0.5f || path == null || pathIndex >= path.size()) {
            pathTimer = 0;

            int[][] map = gameInstance.getCollisionMap();
            int startX = (int) x;
            int startY = (int) y;
            int endX = (int) target.x;
            int endY = (int) target.y;

            path = AStarFinder.findPath(map, startX, startY, endX, endY);
            if (!path.isEmpty()) {
                pathIndex = 1; // Hüppa esimese sihtpunkti peale
            }
        }

        // Liigu järgmise sihtpunkti suunas
        if (path != null && pathIndex < path.size()) {
            Point next = path.get(pathIndex);
            float nextX = next.x + 0.5f; // Keskpunkt ruudus
            float nextY = next.y + 0.5f;

            float dx = nextX - x;
            float dy = nextY - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            float speed = 0.05f; // Liikumiskiirus
            if (distance < 0.2f) {
                pathIndex++; // Kui jõudsid punkti kohale, liigu järgmise juurde
            } else {
                // Liigu punkti suunas
                x += dx / distance * speed * dt;
                y += dy / distance * speed * dt;
            }
        }
    }

    // Leia lähim mängija, kelle suunas liikuda
    private Player getClosestPlayer() {
        float closestDist = Float.MAX_VALUE;
        Player closest = null;
        for (Player p : gameInstance.getPlayers().values()) {
            float dx = p.x - x;
            float dy = p.y - y;
            float dist = dx * dx + dy * dy;
            if (dist < closestDist) {
                closestDist = dist;
                closest = p;
            }
        }
        return closest;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
        logger.info("[BOT {}] Sai {} dmg, elud nüüd: {}/{}", id, amount, health, maxHealth);
    }

    public boolean isDead() {
        return health <= 0;
    }


    // Getterid ja setter
    public float getX() { return x; }
    public float getY() { return y; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getHealth() { return health; }
}
