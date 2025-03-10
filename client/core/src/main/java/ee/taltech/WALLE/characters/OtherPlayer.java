package ee.taltech.WALLE.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class OtherPlayer {
    private int x, y;
    private Texture texture;

    public OtherPlayer() {
        this.texture = new Texture("drop.jpg");
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
