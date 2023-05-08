package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.HashMap;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    /*************
     * Constants *
     *************/
    private static final String AVATAR_TAG = "avatar";
    private static final float TIME_BETWEEN_MOVEMENT = 0.2f;
    private static final float MOVEMENT_SPEED = 200f;
    private static final float JUMP_SPEED = -300f;
    private static final float FALLING_SPEED = 400f;
    private static final float MAXIMUM_ENERGY = 100f;
    private static final float MINIMUM_ENERGY = 0f;
    private static final float ENERGY_RECOVERY_SPEED = 0.5f;
    private static final float MAXIMUM_VERTICAL_SPEED = 500f;

    private static final String STANDING = "standing";
    private static final String WALKING = "walking";
    private static final String JUMPING = "jumping";
    private static final String FLYING = "flying";
    private static final String[] avatarFlyingAdd = {"assets/flying/flying1.png",
            "assets/flying/flying2.png"};
    private static final String[] avatarStandingAdd = {"assets/standing/stand1.png",
            "assets/standing/stand2.png"};
    private static final String[] avatarWalkingAdd = {"assets/walking/walk1.png", "assets/walking/walk2.png"
            , "assets/walking/walk3.png", "assets/walking/walk4.png", "assets/walking/walk5.png"};
    private static final String[] avatarJumpingAdd = {"assets/jumping/jump1.png", "assets/jumping/jump2.png"};
    public static final float AVATAR_SIZE = Block.SIZE * 2;
    /********************
     * Variables Field *
     *******************/
    private Renderable[] avatarFlyingImages, avatarStandingImages, avatarWalkingImages, avatarJumpingImages;
    private static UserInputListener inputListener;
    private final ImageReader imageReader;
    private boolean isLeft = false;
    private final HashMap<String, AnimationRenderable> avatarPoses = new HashMap<>();
    private float energy = 100f;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    private Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                   ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.imageReader = imageReader;
        convertAddressesToImage();
        addPosesToHashMap();
        transform().setAccelerationY(FALLING_SPEED);
//        transform().setVelocityY(-100);
        this.renderer().setRenderable(this.avatarPoses.get(STANDING));
    }

    /**
     * Added all the poses to our Map
     */
    private void addPosesToHashMap() {
        AnimationRenderable avatarStandingAnimation = new AnimationRenderable(avatarStandingImages,
                TIME_BETWEEN_MOVEMENT);
        AnimationRenderable avatarWalkingAnimation = new AnimationRenderable(avatarWalkingImages,
                TIME_BETWEEN_MOVEMENT);
        AnimationRenderable avatarFlyingAnimation = new AnimationRenderable(avatarFlyingImages,
                TIME_BETWEEN_MOVEMENT);
        AnimationRenderable avatarJumpingAnimation = new AnimationRenderable(avatarJumpingImages,
                TIME_BETWEEN_MOVEMENT);

        this.avatarPoses.put(STANDING, avatarStandingAnimation);
        avatarPoses.put(WALKING, avatarWalkingAnimation);
        avatarPoses.put(FLYING, avatarFlyingAnimation);
        avatarPoses.put(JUMPING, avatarJumpingAnimation);

    }

    /**
     * Convert the image address to renderable objects and insert it to the currect array
     */
    private void convertAddressesToImage() {
        avatarFlyingImages = new Renderable[avatarFlyingAdd.length];
        avatarJumpingImages = new Renderable[avatarJumpingAdd.length];
        avatarStandingImages = new Renderable[avatarStandingAdd.length];
        avatarWalkingImages = new Renderable[avatarWalkingAdd.length];
        for (int i = 0; i < avatarStandingAdd.length; i++) {
            avatarStandingImages[i] = imageReader.readImage(avatarStandingAdd[i], true);
        }
        for (int i = 0; i < avatarFlyingAdd.length; i++) {
            avatarFlyingImages[i] = imageReader.readImage(avatarFlyingAdd[i], true);
        }
        for (int i = 0; i < avatarJumpingAdd.length; i++) {
            avatarJumpingImages[i] = imageReader.readImage(avatarJumpingAdd[i], true);
        }
        for (int i = 0; i < avatarWalkingAdd.length; i++) {
            avatarWalkingImages[i] = imageReader.readImage(avatarWalkingAdd[i], true);
        }
    }

    /**
     * Create the avatar for the game
     *
     * @param gameObjects   the gameobject that we want to add the avatar to
     * @param layer         the layer we want to add the avatar
     * @param topLeftCorner the top left corner the avatar will be
     * @param inputListener the inputlistener we use to the keys
     * @param imageReader
     * @return the avatar we created
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {
        Avatar.inputListener = inputListener;
        Avatar avatar = new Avatar(topLeftCorner, new Vector2(AVATAR_SIZE, AVATAR_SIZE), null, imageReader);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);

        avatar.setTag(AVATAR_TAG);
        gameObjects.addGameObject(avatar, layer);

        return avatar;
    }

    /**
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);
        boolean isStanding = true;
        boolean isNotMovingLeft = (!inputListener.isKeyPressed(KeyEvent.VK_LEFT)) &&
                transform().getVelocity().x()<0;
        boolean isNotMovingRight = (!inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) &&
                transform().getVelocity().x()>0;
        boolean isMovingInTwoDirections = inputListener.isKeyPressed(KeyEvent.VK_LEFT) &&
                inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        if (isNotMovingLeft || isNotMovingRight || isMovingInTwoDirections) // stop horizontal movement
            transform().setVelocityX(0);
        else if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) { // move left
            transform().setVelocityX(-MOVEMENT_SPEED);
            isStanding = goingLeft();
        }
        else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) { // move right
            this.transform().setVelocityX(MOVEMENT_SPEED);
            isStanding = goingRight();
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
                energy > MINIMUM_ENERGY) { // flying
            flying();
            return;
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.getVelocity().y() == 0) { //jumping
            energy = Math.max(energy + ENERGY_RECOVERY_SPEED, MAXIMUM_ENERGY);
            this.transform().setVelocityY(JUMP_SPEED);
            this.renderer().setRenderable(this.avatarPoses.get(JUMPING));
        }
        if (transform().getVelocity().y()>MAXIMUM_VERTICAL_SPEED)
            transform().setVelocityY(MAXIMUM_VERTICAL_SPEED);
        finalUpdateOfAvatar(isStanding);

    }

    /**
     * The final changes we want to do for the avatar
     *
     * @param isStanding
     */
    private void finalUpdateOfAvatar(boolean isStanding) {
        if (isStanding) {
            this.renderer().setRenderable(this.avatarPoses.get(STANDING));
        }
    }

    /**
     * what happened when the avatar is flying
     */
    private void flying() {
        energy -= ENERGY_RECOVERY_SPEED;
        this.transform().setVelocityY(-MOVEMENT_SPEED);
        this.renderer().setRenderable(this.avatarPoses.get(FLYING));
    }

    /**
     * What happend when the avatar going right
     *
     * @return false always to say it isn't standing
     */
    private boolean goingRight() {
        this.renderer().setRenderable(this.avatarPoses.get(WALKING));
        if (isLeft) {
            this.renderer().setIsFlippedHorizontally(false);
            isLeft = false;
        }
        return false;
    }

    /**
     * What happend when the avatar going  left
     *
     * @return false always to say it isn't standing
     */
    private boolean goingLeft() {
        this.renderer().setRenderable(this.avatarPoses.get(WALKING));
        if (!isLeft) {
            this.renderer().setIsFlippedHorizontally(true);
            isLeft = true;
        }
        return false;
    }
}
