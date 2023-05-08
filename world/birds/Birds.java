package pepse.world.birds;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.ImageReader;
import danogl.util.Vector2;
import pepse.util.ColumnDrawer;
import pepse.util.NoiseGenerator;
import pepse.world.Block;


public class Birds {
    private static final float BASE_BIRD_HEIGHT = -240f;
    private static final int BIRD_SIZE = 60;

    private static final int RANDOM_EXTRA_HEIGHT = 200;
    private static final double PERLIN_FACTOR = 0.5;
    private GameObjectCollection gameObjects;
    private int birdsLayer;
    private final NoiseGenerator perlinNoise;
    private ImageReader imageReader;

    public Birds(GameObjectCollection gameObjects, int birdsLayer, NoiseGenerator perlinNoise,
                 ImageReader imageReader) {
        this.gameObjects = gameObjects;
        this.birdsLayer = birdsLayer;
        this.perlinNoise = perlinNoise;
        this.imageReader = imageReader;
    }
    public void createInRange(int minX, int maxX) {
        for (int x = ColumnDrawer.roundToSize(minX); x <= ColumnDrawer.roundToSize(maxX); x += Block.SIZE) {
            if (perlinNoise.noise(x)<=PERLIN_FACTOR) continue;
            int extraHeight = (int)((perlinNoise.noise(x))*RANDOM_EXTRA_HEIGHT);
            int birdHeight = (int)(ColumnDrawer.roundToSize(extraHeight) + BASE_BIRD_HEIGHT);
            Vector2 topLeftCorner = new Vector2(x, birdHeight);
            GameObject bird = new Bird(topLeftCorner, Vector2.ONES.mult(BIRD_SIZE), null,
                    imageReader,birdsLayer);
            bird.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            bird.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
            gameObjects.addGameObject(bird, birdsLayer);
        }
    }
}
