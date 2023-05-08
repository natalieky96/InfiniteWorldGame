package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Adds a yellow round GameObject that moves in an elliptic route around the center of the screen.
 */
public class Sun {
    /*************
     * Constants *
     *************/
    private static final float WINDOW_CENTER = 0.5f;

    private static final int SUN_SIZE = 170;
    private static final int WIDTH_RADIUS = 500;
    private static final int HEIGHT_RADIUS = 300;
    private static final Float INITIAL_VALUE = (float) -Math.PI/2;
    private static final Float FINAL_VALUE = (float) Math.PI*1.5f;


    /**
     * creates and adds a sun to the gameobjects collection.
     * @param gameObjects the gameObjects to add the day-night simulator object to.
     * @param layer the layer in which to add the gameObject.
     * @param windowDimensions the dimensions of the screen.
     * @param cycleLength length of the day-night cycle.
     * @return the sun gameObject.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength) {
        // create the sun game object
        OvalRenderable sunImage = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE), sunImage);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        // add the sun to the gameobject collection
        gameObjects.addGameObject(sun, layer);
        // lambda function that calculates the new coordinates of the center of the sun.
        Consumer<Float> findCenter = (Float angle) -> {
            float xCoordinate = windowDimensions.x() * WINDOW_CENTER +
                    (float) Math.cos((double) angle) * WIDTH_RADIUS;
            float yCoordinate = windowDimensions.y() * WINDOW_CENTER +
                    (float) Math.sin((double) angle) * HEIGHT_RADIUS;
            sun.setCenter(new Vector2(xCoordinate, yCoordinate));
        };

        new Transition<Float>(
                sun, // the game object being changed
                findCenter, // the method to call
                INITIAL_VALUE, // initial transition value
                FINAL_VALUE, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength, // transtion fully over half a day
                Transition.TransitionType.TRANSITION_LOOP, // Choose appropriate ENUM value
                null); // nothing further to execute upon reaching final value
        return sun;
    }
}
