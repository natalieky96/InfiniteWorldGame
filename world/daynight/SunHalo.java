package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Creates a halo around the sun object.
 */
public class SunHalo {
    private static final float HALO_SIZE = 300;
    private static GameObject halo;

    /**
     * creates and adds a sun to the gameobjects collection.
     * @param gameObjects the gameObjects to add the day-night simulator object to.
     * @param layer the layer in which to add the gameObject.
     * @param sun the gameObject that represents the sun. this object will be the object which the halo sets
     *           its center according
     * @param color color of the halo.
     * @return the halo gameObject.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){
        // create the halo
        OvalRenderable haloImage = new OvalRenderable(color);
        halo = new GameObject(Vector2.ZERO,new Vector2(HALO_SIZE,HALO_SIZE),haloImage);
        halo.setCenter(sun.getCenter());
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(halo,layer);
        // creates a gameObject that will keep the halo in the center of the sun in every update.
        GameObject haloCenterController = new HaloCenterController(Vector2.ZERO,Vector2.ZERO,null,sun);
        gameObjects.addGameObject(haloCenterController, layer+1);
        return halo;
    }

    /**
     * sets the halo's center in the given Vector2.
     * @param center the new center.
     */
    public static void setHaloCenter(Vector2 center){
        halo.setCenter(center);
    }
}
