package pepse.util;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;

import java.util.Random;

/**
 * Factory incharge of creating instances of blocks and leaves.
 */
public class BlockLeafFactory {
    private static final String GROUND_TYPE = "ground";

    public static GameObject build(String type, Vector2 topLeftCorner, Renderable image, int layer,
                                   Random random) {
        GameObject gameObject = null;
        switch (type) {
            case Tree.LEAF_TAG:
                Leaf leafObject = new Leaf(topLeftCorner, image,random);
                leafObject.setLayer(layer);
                gameObject = leafObject;
                break;
            case GROUND_TYPE:
                Block groundObject = new Block(topLeftCorner, image);
                groundObject.setLayer(layer);
                gameObject = groundObject;
                break;
            case Tree.TRUNK_TAG:
                Block trunkObject = new Block(topLeftCorner, image);
                trunkObject.setLayer(layer);
                gameObject = trunkObject;
                break;
        }
        return gameObject;
    }
}
