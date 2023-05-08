package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.util.ColumnDrawer;
import pepse.util.CoordinatesDisplayer;
import pepse.util.DeleteObjectsOutOfScreen;
import pepse.util.NoiseGenerator;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.birds.Birds;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {
    /*************
     * Constants *
     *************/
    private static final float HALF = 0.5f;
    private static final Color HALO_COLOR = new Color(225, 210, 135, 50);
    private static final int SUN_LAYER = Layer.BACKGROUND + 10;
    private static final int HALO_LAYER = Layer.BACKGROUND + 20;
    private static final int TREE_TRUNK_LAYER = Layer.STATIC_OBJECTS + 10;
    private static final int LEAF_LAYER = TREE_TRUNK_LAYER + 10;
    private static final float NIGHT_CYCLE_LENGTH = 50;
    private static final float SUN_CYCLE_LENGTH = 100;
    private static final Vector2 COORDINATES_DISPLAYER_VECTOR = new Vector2(70, 20);
    private static final int SEED = 2;
    /*************
     * Fields *
     *************/
    private UserInputListener userInputListener;
    private WindowController windowController;
    private ImageReader imageReader;
    private float avatarCoordinateX;
    private float avatarCoordinateY;
    private Terrain terrain;
    private Avatar avatar;
    private Tree tree;
    private Birds birds;
    private static final float SCREEN_WIDTH = 1280;
    private static final float SCREEN_HEIGHT = 720;
    private static final String WINDOW_TITLE = "Best Game";
    private float cameraLeftLoadedScreen;
    private float cameraRightLoadedScreen;

//    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
//        super(windowTitle, windowDimensions);
//    }

    //    public static void main(String[] args) {
//        new PepseGameManager(WINDOW_TITLE,new Vector2(SCREEN_WIDTH,SCREEN_HEIGHT)).run();
//    }
    public static void main(String[] args) {
//        new PepseGameManager(WINDOW_TITLE,new Vector2(SCREEN_WIDTH,SCREEN_HEIGHT)).run();
        new PepseGameManager().run();
    }


    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.userInputListener = inputListener;
        this.windowController = windowController;
        this.imageReader = imageReader;

        // create Sky
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);

        // create Terrain
        terrain = new Terrain(gameObjects(),
                Layer.STATIC_OBJECTS,
                windowController.getWindowDimensions(),
                SEED);
        NoiseGenerator perlinNoise = terrain.getPerlinNoise();

        // create Night
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(),
                NIGHT_CYCLE_LENGTH);

        // create sun
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(),
                SUN_CYCLE_LENGTH);

        // create sunHalo
        SunHalo.create(gameObjects(), HALO_LAYER, sun, HALO_COLOR);

        // create Avatar
        avatar = createAvatar();

        // create trees
        tree = new Tree(terrain::groundHeightAt, gameObjects(), TREE_TRUNK_LAYER, LEAF_LAYER,
                avatarCoordinateX, avatarCoordinateX + Avatar.AVATAR_SIZE, perlinNoise, SEED);

        // create birds
        birds = new Birds(gameObjects(), Layer.STATIC_OBJECTS, perlinNoise, imageReader);

        // build terrain, trees and birds.
        createInRange(0, (int) windowController.getWindowDimensions().x());
        // set the game's layers collision for layers who should and shouldnt collide with one another.
        setLayersCollision();

        // set the camera on avatar.
        setCameraOnAvatar();

        // displays user coordinates on the screen
        CoordinatesDisplayer coordinatesDisplayer = new CoordinatesDisplayer(Vector2.ZERO,
                COORDINATES_DISPLAYER_VECTOR, this.getCamera());
        gameObjects().addGameObject(coordinatesDisplayer);

        // initialize DeleteObjectsOutOfScreen
        DeleteObjectsOutOfScreen.createDeleteObjectsOutOfScreen(gameObjects(), getCamera());

        // update range of loaded screen.
        cameraLeftLoadedScreen = 0;
        cameraRightLoadedScreen = ColumnDrawer.roundToSize(windowController.getWindowDimensions().x());
    }

    /*
    sets the camera to follow the avatar.
     */
    private void setCameraOnAvatar() {
        float cameraXCoordinate = windowController.getWindowDimensions().x() * HALF - avatarCoordinateX;
        float cameraYCoordinate = windowController.getWindowDimensions().y() * HALF - avatarCoordinateY;
        Vector2 initialCameraPosition = new Vector2(cameraXCoordinate, cameraYCoordinate);
        this.setCamera(new Camera(
                avatar,
                initialCameraPosition,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()
        ));
    }

    /*
    calculates the avatar's start coordinates and creates it.
     */
    private Avatar createAvatar() {
        avatarCoordinateX =
                ColumnDrawer.roundToSize(windowController.getWindowDimensions().x() * HALF);
        avatarCoordinateY = Math.min(terrain.groundHeightAt(avatarCoordinateX),
                terrain.groundHeightAt(avatarCoordinateX + Block.SIZE)) - Avatar.AVATAR_SIZE;
        Vector2 avatarCoordinates = new Vector2(avatarCoordinateX, avatarCoordinateY);
        return Avatar.create(gameObjects(), Layer.DEFAULT, avatarCoordinates, this.userInputListener,
                this.imageReader);
    }

    /*
    sets the layer's collisions settings.
     */
    private void setLayersCollision() {
        gameObjects().layers().shouldLayersCollide(Layer.STATIC_OBJECTS, LEAF_LAYER, true);
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, TREE_TRUNK_LAYER, true);
        gameObjects().layers().shouldLayersCollide(Layer.STATIC_OBJECTS, Layer.STATIC_OBJECTS, false);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, LEAF_LAYER, false);
        gameObjects().layers().shouldLayersCollide(TREE_TRUNK_LAYER, TREE_TRUNK_LAYER, false);
        gameObjects().layers().shouldLayersCollide(Layer.STATIC_OBJECTS - 1, Layer.STATIC_OBJECTS - 1, false);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float screenDimensionsX = ColumnDrawer.roundToSize(this.getCamera().windowDimensions().x());
        float newScreenLoadBlocksRange = screenDimensionsX / 5;
        float cameraLeftXCoordinate = ColumnDrawer.roundToSize(this.getCamera().getTopLeftCorner().x());
        float cameraRightXCoordinate =
                ColumnDrawer.roundToSize(this.getCamera().getTopLeftCorner().x() + screenDimensionsX);
        // update the left corner of the loaded world
        if (cameraLeftLoadedScreen < cameraLeftXCoordinate - newScreenLoadBlocksRange)
            cameraLeftLoadedScreen = cameraLeftXCoordinate - newScreenLoadBlocksRange;
        // expand the loaded world from the left side of the screen.
        if (cameraLeftXCoordinate - newScreenLoadBlocksRange < cameraLeftLoadedScreen) {
            // saves the left x coordinate of the loaded screen before new range needs to be created
            float oldCameraLeftLoadedScreen = cameraLeftLoadedScreen;
            cameraLeftLoadedScreen = cameraLeftXCoordinate - newScreenLoadBlocksRange;
            createInRange((int) cameraLeftLoadedScreen,
                    (int) oldCameraLeftLoadedScreen);
        }
        // update the right corner of the loaded world
        if (cameraRightLoadedScreen > cameraRightXCoordinate + newScreenLoadBlocksRange)
            cameraRightLoadedScreen = cameraRightXCoordinate + newScreenLoadBlocksRange;
        // expand the loaded world from the right side of the screen.
        if (cameraRightXCoordinate + newScreenLoadBlocksRange > cameraRightLoadedScreen) {
            // saves the right x coordinate of the loaded screen before new range needs to be created
            float oldCameraRightLoadedScreen = cameraRightLoadedScreen;
            cameraRightLoadedScreen = cameraRightXCoordinate + newScreenLoadBlocksRange;
            createInRange((int) oldCameraRightLoadedScreen,
                    (int) cameraRightLoadedScreen);
        }
    }

    /*
    create objects in new discovered world.
     */
    private void createInRange(int minX, int maxX) {
        terrain.createInRange(minX, maxX);
        tree.createInRange(minX, maxX);
        birds.createInRange(minX, maxX);
    }
}
