package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

/**
 * Simulates day-night cycle.
 */
public class Night {
    private static final String NIGHT_TAG="night";
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final Float DAY_STATUS = 0f;


    /**
     * creates and adds a gameObject that simulates the day-night cycle.
     * @param gameObjects the gameObjects to add the day-night simulator object to.
     * @param layer the layer in which to add the gameObject.
     * @param windowDimensions the dimensions of the screen.
     * @param cycleLength length of the day-night cycle.
     * @return the night object that simualates the day-night cycle.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        // create a night gameObject and adds it to the gameObjects collection.
        Renderable nightImage = new RectangleRenderable(ColorSupplier.approximateColor(Color.BLACK));
        GameObject night = new GameObject(Vector2.ZERO,windowDimensions,nightImage);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        gameObjects.addGameObject(night,layer);
        // defines the day-night transition.
        new Transition<Float>(
                night, // the game object being changed
                night.renderer()::setOpaqueness, // the method to call
                DAY_STATUS, // initial transition value
                MIDNIGHT_OPACITY, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength, // transtion fully over half a day
        Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
        null); // nothing further to execute upon reaching final value
        return night;
    }
}
