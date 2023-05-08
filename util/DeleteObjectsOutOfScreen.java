package pepse.util;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Camera;
import pepse.world.Block;

/**
 * removes a given object from the screen
 */
public class DeleteObjectsOutOfScreen {
    /*************
     * Constants *
     *************/
    private static final int LEAVES_FACTOR = 4;
    /*************
     * Static Objects *
     *************/
    public static DeleteObjectsOutOfScreen deleteObjectsOutOfScreen;
    private static GameObjectCollection gameObjects;
    private static Camera camera;

    /*
    constructor
     */
    private DeleteObjectsOutOfScreen(GameObjectCollection gameObjects, Camera camera) {
        DeleteObjectsOutOfScreen.camera = camera;
        DeleteObjectsOutOfScreen.gameObjects = gameObjects;
    }

    /**
     * creates a single instance of the class
     *
     * @param gameObjects the collection from which to the remove objects.
     * @param camera      the camera that views the game - used to track the boundraies of the screen at
     *                   each time.
     */
    public static void createDeleteObjectsOutOfScreen(GameObjectCollection gameObjects, Camera camera) {
        if (deleteObjectsOutOfScreen == null)
            deleteObjectsOutOfScreen = new DeleteObjectsOutOfScreen(gameObjects, camera);
    }

    /**
     * deletes an object from the objects collection it's distance from the screen is more than a full
     * screen width.
     *
     * @param gameObjectToDelete the object to remove.
     * @param objectLayer        the layer from which to remove the given object.
     */
    public static void deleteObject(GameObject gameObjectToDelete, int objectLayer) {
        // x coordinate of the object to remove
        float objectCoordinatesX = ColumnDrawer.roundToSize(gameObjectToDelete.getTopLeftCorner().x());
        float windowDimensionX = ColumnDrawer.roundToSize(camera.windowDimensions().x());
        float newScreenLoadBlocksRange = windowDimensionX/5;

        // left and right boundaries of the screen covered by the camera.
        float cameraLeftXCoordinate = ColumnDrawer.roundToSize(camera.getTopLeftCorner().x());
        float cameraRightXCoordinate =
                ColumnDrawer.roundToSize(camera.getTopLeftCorner().x() + windowDimensionX);
        // removes the object from the screen if it in a given distance from the left or right side of
        // the screen.
        if (cameraLeftXCoordinate - newScreenLoadBlocksRange - LEAVES_FACTOR * Block.SIZE > objectCoordinatesX
                || cameraRightXCoordinate + newScreenLoadBlocksRange +
                LEAVES_FACTOR * Block.SIZE < objectCoordinatesX) {
            gameObjects.removeGameObject(gameObjectToDelete, objectLayer);
        }
    }
}
