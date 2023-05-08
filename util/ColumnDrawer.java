package pepse.util;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;

/**
 * Class in charge of creating a column of blocks or leaves in a given x coordinate.
 */
public class ColumnDrawer {
    private static final int TOP_GROUND_SIZE = 2;

    /**
     * rounds a number to the minimum closest number that divides by block size.
     * @param numToRound a number to round
     * @return the minimum closest number that divides numToRound by block size.
     */
    public static int roundToSize(float numToRound) {
        return (int) Math.floor(numToRound / Block.SIZE) * Block.SIZE;
    }

    /**
     * Create a column of @columnLength objects in a vertical line at coordinate x, when the top left y
     * coordinate of the column starts at topHeight, and going up.
     * @param color the color of the objects in the column.
     * @param x the x coordinate in which to create the objects.
     * @param topHeight the top most y coordinate of the column.
     * @param columnLength the amount of objects in the column.
     * @param gameObjects the gameObject to add the objects to.
     * @param tagName the tag name of the objects.
     * @param objectLayer the layer in which to create the objects.
     */
    public static void createColumnOfObjects(Color color, int x, int topHeight,
                                             int columnLength, GameObjectCollection gameObjects,
                                             String tagName, int objectLayer, Random random) {
        int currentObjectLayer =objectLayer;
        int blocksCount = 0;
        // iterate over columnLength to create @columnLength objects.
        while (blocksCount < columnLength) {
            // if the tag name is Terrain.GROUND_TAG_NAME, set the TOP_GROUND_SIZE blocks in a different
            // layer.
            if (tagName.equals(Terrain.GROUND_TAG_NAME) && blocksCount > TOP_GROUND_SIZE){
                currentObjectLayer =objectLayer-1;
            }
            // set each object's location and renderable.
            Vector2 topLeftCorner = new Vector2(x, topHeight);
            Renderable image = new RectangleRenderable(ColorSupplier.approximateColor(color));
            // create the object using factory.
            GameObject objectToCreate = BlockLeafFactory.build(tagName,topLeftCorner, image,
                    currentObjectLayer,random);
            objectToCreate.setTag(tagName);
            gameObjects.addGameObject(objectToCreate, currentObjectLayer);
            // advance to the next object to add.
            topHeight += Block.SIZE;
            blocksCount++;
        }
    }
}
