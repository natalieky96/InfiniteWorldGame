package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import pepse.util.NoiseGenerator;
import pepse.world.Block;
import pepse.util.ColumnDrawer;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;

public class Tree {
    /*************
     * Constants *
     *************/
    private final Random random;
    private static final int BASE_TREE_HEIGHT = 10;
    private static final int RANDOM_EXTRA_HEIGHT = 10;
    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    public static final String TRUNK_TAG = "trunk";
    public static final String LEAF_TAG = "leaf";
    private static final double PERLIN_FACTOR = 0.35;
    private static final float PERLIN_DIVISION_FACTOR = 30f;
    private static final int LEAVES_PART_OF_TREE = 3;
    private static final int LEAVES_ROW_SIZE = 2;

    /*************
     * Fields *
     *************/
    private final float avatarXRightCoord; // the initial x coordinate of the avatar. trees will not be planted in that
    // coordinate.
    private final NoiseGenerator perlinNoise;
    private int seed;
    private final Function<Float, Float> treeBaseHeight;
    private final int leafLayer;
    private final GameObjectCollection gameObjects;
    private final int trunkLayer;
    private final float avatarXLeftCoord;

    /**************
     * Constructor *
     **************/
    public Tree(Function<Float, Float> treeBaseHeight, GameObjectCollection gameObjects, int trunkLayer,
                int leafLayer, float avatarXLeftCoord, float avatarXRightCoord, NoiseGenerator perlinNoise,
                int seed) {

        this.treeBaseHeight = treeBaseHeight;
        this.leafLayer = leafLayer;
        this.gameObjects = gameObjects;
        this.trunkLayer = trunkLayer;
        this.avatarXLeftCoord = avatarXLeftCoord;
        this.avatarXRightCoord = avatarXRightCoord;
        this.perlinNoise = perlinNoise;
        this.seed = seed;
        this.random = new Random(seed);
    }

    public void createInRange(int minX, int maxX) {
        for (int x = ColumnDrawer.roundToSize(minX); x < ColumnDrawer.roundToSize(maxX); x += Block.SIZE) {

            if (perlinNoise.noise(x)<=PERLIN_FACTOR || (x >= avatarXLeftCoord && x <= avatarXRightCoord)) continue;
            int extraHeight = (int) (perlinNoise.noise(x/PERLIN_DIVISION_FACTOR)*RANDOM_EXTRA_HEIGHT);
            int treeHeight = extraHeight + BASE_TREE_HEIGHT;
            float columnBase = treeBaseHeight.apply(Integer.valueOf(x).floatValue());
            int topHeight = (int) columnBase - treeHeight * Block.SIZE;
            ColumnDrawer.createColumnOfObjects(TRUNK_COLOR, x, topHeight,
                    treeHeight, gameObjects, TRUNK_TAG, trunkLayer,random);
            createLeaves(x, treeHeight, topHeight);
        }
    }

    private void createLeaves(int x, int treeHeight, int treeTopHeight) {

        int leavesWidth = (treeHeight / LEAVES_PART_OF_TREE) * Leaf.SIZE;
        for (int i = x - leavesWidth; i <= x + leavesWidth; i += Leaf.SIZE) {
            int columnLength = (LEAVES_ROW_SIZE * leavesWidth) / Leaf.SIZE;
            int leaveTop = treeTopHeight - leavesWidth;
            ColumnDrawer.createColumnOfObjects(LEAF_COLOR, i, leaveTop,
                    columnLength, gameObjects, LEAF_TAG, leafLayer,random);
        }
    }
}
