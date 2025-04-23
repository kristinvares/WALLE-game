package ee.taltech.walle.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ee.taltech.walle.walleGame;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private Integer worldTimer;
    private Integer score;

    Label countdownLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label playerLabel;

    public Hud(SpriteBatch sb) {
        worldTimer = 300;
        score = 0;
        viewport = new FitViewport(walleGame.V_WIDTH, walleGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        BitmapFont font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        timeLabel = new Label("TIME", style);  // Tee lables
        worldLabel = new Label("WORLD", style);
        playerLabel = new Label("Player", style);

        countdownLabel = new Label(String.format("%03d", worldTimer), style);
        levelLabel = new Label("1-1", style);

        // Ülemine rida (pealkirjad)
        table.add(playerLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);

        // Alumine rida (väärtused)
        table.row();

        Texture heartTexture = new Texture("health/heart_animated_elud_tais.png");
        Image heartImage = new Image(heartTexture);
        table.add(heartImage).expandX(); // südamed Player all

        table.add(levelLabel).expandX();  // level WORLD all
        table.add(countdownLabel).expandX();  // taimer TIME all

        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
