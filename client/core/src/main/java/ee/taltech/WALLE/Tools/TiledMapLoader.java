package ee.taltech.WALLE.Tools;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class TiledMapLoader {
    private TiledMap map;

    /**
     * @param path path to the map, starting from /assets folder
     */
    public TiledMapLoader(String path) {
        this.map = new TmxMapLoader().load(path);
    }

    /**
     * @return OrthogonalTiledMapRenderer of the TiledMap
     */
    public OrthogonalTiledMapRenderer setupMap() {
        return new OrthogonalTiledMapRenderer(map, 1 / ee.taltech.WALLE.WALLEGame.PPM);
    }

    /**
     * Saame kaardi viite
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Puhastab ressursid
     */
    public void dispose() {
        if (map != null) {
            map.dispose();
            map = null;
        }
    }

    /**
     * Parse all objects from all layers of the TiledMap.
     */
    public TiledMapLoader parseAllObjects() {
        for (MapLayer mapLayer : map.getLayers()) {
            for (MapObject mapObject : mapLayer.getObjects()) {
                if (mapObject instanceof RectangleMapObject rectangleMapObject) {
                    Rectangle rectangle = rectangleMapObject.getRectangle();
                    // Töötlus, kui on vaja
                }
            }
        }
        return this;
    }
}
