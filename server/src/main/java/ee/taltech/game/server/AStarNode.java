package ee.taltech.game.server;

public class AStarNode {
    public int x;
    public int y;
    public AStarNode parent; // Eelnev sõlm teel (tee rekonstrueerimiseks)
    public float g; // Kaugus alguspunktist (reaalne kulu siiani)
    public float h; // Hinnanguline kaugus sihtpunkti (heuristik)

    public AStarNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // f = g + h ehk üldine kulu: kaugus + hinnang
    public float f() {
        return g + h;
    }

    @Override
    public boolean equals(Object o) {
        // Kontrollib, kas koordinaadid on samad
        if (!(o instanceof AStarNode)) return false;
        AStarNode other = (AStarNode) o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        // Lihtne hashCode kahe koordinaadi põhjal
        return x * 1000 + y;
    }
}
