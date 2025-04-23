package ee.taltech.game.server;

import java.awt.Point;
import java.util.*;

public class AStarFinder {

    public static List<Point> findPath(int[][] map, int startX, int startY, int endX, int endY) {
        PriorityQueue<AStarNode> open = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::f));
        Map<Point, AStarNode> allNodes = new HashMap<>();

        AStarNode startNode = new AStarNode(startX, startY);
        startNode.g = 0;
        startNode.h = heuristic(startX, startY, endX, endY);
        open.add(startNode);
        allNodes.put(new Point(startX, startY), startNode);

        while (!open.isEmpty()) {
            AStarNode current = open.poll();

            if (current.x == endX && current.y == endY) {
                return buildPath(current);
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue; // Ära lisa iseend

                    int nx = current.x + dx;
                    int ny = current.y + dy;

                    // Kontrolli kaardi piire
                    if (nx < 0 || ny < 0 || nx >= map.length || ny >= map[0].length) continue;
                    if (map[nx][ny] == 1) continue; // Väldi seina

                    // Arvuta hind: diagonaal = 1.414, muidu 1
                    float cost = (dx == 0 || dy == 0) ? 1f : 1.414f;

                    Point neighborPoint = new Point(nx, ny);
                    float newG = current.g + cost;

                    AStarNode neighbor = allNodes.getOrDefault(neighborPoint, new AStarNode(nx, ny));
                    if (newG < neighbor.g || !allNodes.containsKey(neighborPoint)) {
                        neighbor.g = newG;
                        neighbor.h = heuristic(nx, ny, endX, endY);
                        neighbor.parent = current;
                        allNodes.put(neighborPoint, neighbor);
                        open.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList(); // tee ei leitud
    }

    private static float heuristic(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return dx + dy + (1.414f - 2f) * Math.min(dx, dy);
    }

    private static List<Point> buildPath(AStarNode endNode) {
        LinkedList<Point> path = new LinkedList<>();
        AStarNode current = endNode;
        while (current != null) {
            path.addFirst(new Point(current.x, current.y));
            current = current.parent;
        }
        return path;
    }
}
