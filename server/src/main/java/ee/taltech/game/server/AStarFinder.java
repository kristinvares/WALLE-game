package ee.taltech.game.server;

import java.awt.Point;
import java.util.*;

public class AStarFinder {

    // Peamine meetod tee leidmiseks
    public static List<Point> findPath(int[][] map, int startX, int startY, int endX, int endY) {
        // Avatud sõlmed, järjestatud f() väärtuse järgi
        PriorityQueue<AStarNode> open = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::f));

        // Kõik külastatud sõlmed, et vältida kordusi
        Map<Point, AStarNode> allNodes = new HashMap<>();

        // Alguspunkt
        AStarNode startNode = new AStarNode(startX, startY);
        startNode.g = 0;
        startNode.h = heuristic(startX, startY, endX, endY); // kaugushinnang sihtpunkti
        open.add(startNode);
        allNodes.put(new Point(startX, startY), startNode);

        while (!open.isEmpty()) {
            AStarNode current = open.poll(); // võta madalaima f() väärtusega sõlm

            // Kui sihtpunkt on saavutatud, ehita tee tagasi
            if (current.x == endX && current.y == endY) {
                return buildPath(current);
            }

            // Lisa naabrid töötlemiseks
            addValidNeighbors(current, map, endX, endY, open, allNodes);
        }

        return Collections.emptyList(); // Kui tee ei leitud
    }

    // Lisa kehtivad naabrid praegusele sõlmele
    private static void addValidNeighbors(AStarNode current, int[][] map, int endX, int endY,
                                          PriorityQueue<AStarNode> open, Map<Point, AStarNode> allNodes) {

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = current.x + dx;
                int ny = current.y + dy;
                float cost = (dx == 0 || dy == 0) ? 1f : 1.414f;
                float newG = current.g + cost;

                Point neighborPoint = new Point(nx, ny);
                AStarNode neighbor = allNodes.getOrDefault(neighborPoint, new AStarNode(nx, ny));

                // Kui uus tee on lühem või sõlm on uus
                if (newG < neighbor.g || !allNodes.containsKey(neighborPoint)) {
                    neighbor.g = newG;
                    neighbor.h = heuristic(nx, ny, endX, endY);
                    neighbor.parent = current;
                    allNodes.put(neighborPoint, neighbor); // pane sõlm nimekirja
                    open.add(neighbor);  // lisa töötlusse
                }
            }
        }
    }
    // Heuristiline kauguse hinnang (diagonaalide toega)
    private static float heuristic(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return dx + dy + (1.414f - 2f) * Math.min(dx, dy);  // diagonaalne kaugus
    }

    // Ehitab tagantjärele tee sihtpunktist alguspunkti
    private static List<Point> buildPath(AStarNode endNode) {
        LinkedList<Point> path = new LinkedList<>();
        AStarNode current = endNode;
        while (current != null) {
            path.addFirst(new Point(current.x, current.y)); // lisa algusesse, et saada õige järjekord
            current = current.parent;
        }
        return path;
    }

}
