package ee.taltech.game.server;

import networks.Player;

import java.awt.Point;
import java.util.List;

public class Bot {
    private float x, y;
    private List<Point> path;
    private int pathIndex;
    private int id;

    private GameInstance gameInstance;

    public Bot(GameInstance gameInstance, float x, float y) {
        this.gameInstance = gameInstance;
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
        if (gameInstance.getPlayers().isEmpty()) return;

        // Leia lähim mängija
        float closestDist = Float.MAX_VALUE;
        float targetX = x, targetY = y;

        for (Player player : gameInstance.getPlayers().values()) {
            float dx = player.x - x;
            float dy = player.y - y;
            float dist = dx * dx + dy * dy;

            if (dist < closestDist) {
                closestDist = dist;
                targetX = player.x;
                targetY = player.y;
            }
        }

        // Arvuta liikumissuund
        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.1f) return; // juba piisavalt lähedal

        float speed = 1.5f; // tile/sec või world unit/sec
        x += dx / distance * speed * dt;
        y += dy / distance * speed * dt;
    }

    public void setPath(List<Point> path) {
        this.path = path;
        this.pathIndex = 0;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }
}
