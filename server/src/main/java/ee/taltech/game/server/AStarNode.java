package ee.taltech.game.server;

public class AStarNode {
    public int x, y;
    public AStarNode parent;
    public float g, h;

    public AStarNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public float f() {
        return g + h;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AStarNode)) return false;
        AStarNode other = (AStarNode) o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return x * 1000 + y;
    }
}
