package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Creates Sky in the size of the screen, that follows the camera's view.
 */
public class Sky {
    private static final Color BASIC_SKY_COLOR= Color.decode("#80C6E5");
    private static final String SKY_TAG_NAME = "sky";

    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions, int skyLayer){
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG_NAME);
        gameObjects.addGameObject(sky,skyLayer);
        return sky;
    }
}
