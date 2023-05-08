package pepse.world.birds;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.DeleteObjectsOutOfScreen;

public class Bird extends GameObject {
    private static final String[] birdFlyingAdd = {"assets/angry_birds/angryBird1.png",
            "assets/angry_birds/angryBird2.png", "assets/angry_birds/angryBird3.png",
            "assets/angry_birds/angryBird4.png", "assets/angry_birds/angryBird5.png",
            "assets/angry_birds/angryBird6.png", "assets/angry_birds/angryBird7.png",
            "assets/angry_birds/angryBird8.png"};
    public static float TIME_BETWEEN_MOVEMENT = 0.5f;

    private final ImageReader imageReader;
    private int layer;
    private UserInputListener inputListener;
    private Renderable[] birdFlyingImages;
    private final Vector2 startBirdCoordinates;
    private boolean isRight;
    private static final float HOW_FAR_TO_GO = 1000f;
    private static final float MOVEMENT_SPEED = 150f;

    public Bird(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, ImageReader imageReader,
                int layer) {
        super(topLeftCorner, dimensions, renderable);
        this.imageReader = imageReader;
        this.layer = layer;
        this.inputListener = inputListener;
        birdFlyingImages = new Renderable[birdFlyingAdd.length];
        for (int i = 0; i < birdFlyingAdd.length; i++) {
            birdFlyingImages[i] = imageReader.readImage(birdFlyingAdd[i], true);
        }
        AnimationRenderable birdAnimation = new AnimationRenderable(this.birdFlyingImages,
                TIME_BETWEEN_MOVEMENT);
        this.startBirdCoordinates = getCenter();
        isRight = true;
        this.renderer().setRenderable(birdAnimation);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // remove the bird from the gameObjects if it is too far from the camera's view.
        DeleteObjectsOutOfScreen.deleteObject(this, layer);
        Vector2 movementDir = Vector2.ZERO;
        if (isRight) {
            movementDir = movementDir.add(Vector2.RIGHT);
        } else {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if (this.getCenter().x() > startBirdCoordinates.x() + HOW_FAR_TO_GO) {
            this.renderer().setIsFlippedHorizontally(true);
            isRight = false;
        }
        if (this.getCenter().x() < this.startBirdCoordinates.x()) {
            this.renderer().setIsFlippedHorizontally(false);
            isRight = true;
        }
        this.setVelocity(movementDir.mult(MOVEMENT_SPEED));


    }
}
