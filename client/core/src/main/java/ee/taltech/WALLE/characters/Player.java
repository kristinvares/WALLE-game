package ee.taltech.WALLE.characters;

import java.io.Serializable;

public class Player implements Serializable {
    private int id;
    private int x, y;

    public Player(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getPositionString() {
        return x + "," + y;
    }
}


