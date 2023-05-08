package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.util.ColumnDrawer;
import pepse.util.NoiseGenerator;


import java.awt.*;

/**
 * Incharge of creating the game's ground.
 */
public class Terrain {
    /*************
     * Constants *
     *************/
    private static final float HEIGHT_MULT_FACTOR = 2.0f / 3;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    public static final String GROUND_TAG_NAME = "ground";
    private static final float PERLIN_FACTOR = 0.05f;
    private static final double PERLIN_DIFFERENCE_FACTOR = 7;

    /*******************
     * Field Variables *
     *******************/
    public final NoiseGenerator perlinNoise;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final int seed;

    /*******************
     * Constructor *
     *******************/
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.perlinNoise = new NoiseGenerator(seed);
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        groundHeightAtX0 = windowDimensions.y() * HEIGHT_MULT_FACTOR;
        this.seed = seed;
    }

    /**
     * Return the height of the terrain in given x
     *
     * @param x the x we want to check the height at
     * @return the height as a float at the given x
     */
    public float groundHeightAt(float x) {
        double yValue = perlinNoise.noise(x * PERLIN_FACTOR);
        return ColumnDrawer.roundToSize(groundHeightAtX0 +
                (float) (yValue * PERLIN_DIFFERENCE_FACTOR * Block.SIZE));
    }

    /**
     * Create columns from the minX to the maX
     *
     * @param minX the point of starting the range
     * @param maxX the point of ending of the range
     */
    public void createInRange(int minX, int maxX) {
        int topHeight;

        for (int x = ColumnDrawer.roundToSize(minX); x < ColumnDrawer.roundToSize(maxX); x += Block.SIZE) {
            topHeight = ColumnDrawer.roundToSize(groundHeightAt(x));

            ColumnDrawer.createColumnOfObjects(BASE_GROUND_COLOR, x, topHeight,
                    TERRAIN_DEPTH, gameObjects, GROUND_TAG_NAME, groundLayer,null);
        }
    }

    /**
     * Getter for the perlin Noise
     */
    public NoiseGenerator getPerlinNoise() {
        return perlinNoise;
    }
}
