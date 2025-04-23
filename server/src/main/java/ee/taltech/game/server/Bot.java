package ee.taltech.game.server;

import networks.Player;
import java.awt.Point;
import java.util.List;

public class Bot {
    private float x, y;
    private int id;

    private GameInstance gameInstance;

    private List<Point> path;
    private int pathIndex = 0;
    private float pathTimer = 0;

    public Bot(GameInstance gameInstance, float x, float y) {
        this.gameInstance = gameInstance;
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
        System.out.println("🤖 [BOT " + id + "] Alustas update(). Pos: (" + x + ", " + y + ")");

        if (gameInstance.getPlayers().isEmpty()) {
            System.out.println("⚠️ [BOT " + id + "] Mängijaid ei ole.");
            return;
        }

        if (gameInstance.getCollisionMap() == null) {
            System.out.println("⚠️ [BOT " + id + "] Collision map puudub!");
            return;
        }

        Player target = getClosestPlayer();
        if (target == null) return;

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
                pathIndex = 1;
                System.out.println("✅ [BOT " + id + "] Leitud tee, pikkus: " + path.size());
            } else {
                System.out.println("❌ [BOT " + id + "] Tee puudub.");
            }
        }

        if (path != null && pathIndex < path.size()) {
            Point next = path.get(pathIndex);
            float nextX = next.x + 0.5f;
            float nextY = next.y + 0.5f;

            float dx = nextX - x;
            float dy = nextY - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            float speed = 0.05f;
            if (distance < 0.2f) {
                pathIndex++;
            } else {
                x += dx / distance * speed * dt;
                y += dy / distance * speed * dt;
            }
        }
    }

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

    public float getX() { return x; }
    public float getY() { return y; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
