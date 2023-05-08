package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.DeleteObjectsOutOfScreen;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.Random;

public class Leaf extends GameObject {
    /*************
     * Constants *
     *************/
    private static final int BASIC_DEATH_TIME = 5;
    private static final int EXTRA_RANDOM_DEATH_TIME = 15;
    private static final int EXTRA_RANDOM_LIFE_TIME = 100;
    public static final int SIZE = 30;
    private static final float FADEOUT_TIME = 20;
    private static final float FALLOUT_VELOCITY = 100;
    private static final float FULLY_VISIBLE = 1;
    private static final Float INITIAL_ANGLE = 0f;
    private static final float FINAL_ANGLE = 20f;
    private static final float TRANSITION_CYCLE_LENGTH = 3;
    private static final Vector2 INITIAL_DIMENSIONS = new Vector2(30,30);
    private static final Vector2 FINAL_DIMENSIONS = new Vector2(27,30);
    /*************
     * Fields *
     *************/
    private final Vector2 startLeafCoordinates; // the coordinates of the leaf on the tree
    private final Random random;
    private boolean shouldCollideWithGround = false; // used in shouldCollideWith to determine whether the game should
    // look for collisions of the leaf and the ground. will be updated to true while the leaf in falling off the tree.
    private Transition<Float> angleTransition;
    private Transition<Vector2> horizontalTransition;
    private int layer; // the layer the leaf is in the gameObjects collection. will be used for removing the leaf
    // from the gameObjects when it is far from the camera view.

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param renderable    The renderable representing the object.
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable,Random random) {
        super(topLeftCorner, Vector2.ONES.mult(Block.SIZE), renderable);
        this.random = random;
        this.startLeafCoordinates = this.getCenter();
        // set leaf transitions
        float waitTime = random.nextFloat();
        new ScheduledTask(
                this, waitTime, false, this::transition);
        // set leaf life cycle
        leafLifeCycle();

    }

    /*
    starts the life-death cycle of the leaf.
     */
    private void leafLifeCycle(){
        int extraLifeTime = random.nextInt(EXTRA_RANDOM_LIFE_TIME);
        // after a certain amount of time, start the dropping of the tree cycle.
        new ScheduledTask(
                this, extraLifeTime, false, this::dropOut);
    }

    /*
       starts the cycle of dropping of the tree.
     */
    private void dropOut(){
        shouldCollideWithGround = true;
        this.renderer().fadeOut(FADEOUT_TIME,this::reBirth);
        this.transform().setVelocityY(FALLOUT_VELOCITY);
    }

    /*
        waits a random number of seconds and then grow back on the tree.
     */
    private void reBirth(){
        int extraDeathTime = random.nextInt(EXTRA_RANDOM_DEATH_TIME);
        Runnable putLeafBackOnTree = ()->{
                this.renderer().setOpaqueness(FULLY_VISIBLE); // make the leaf visible again.
                this.setCenter(startLeafCoordinates); // move the leaf back on the tree.
                this.addComponent(horizontalTransition); // make the leaf horizontally flip sides.
                this.addComponent(angleTransition); // make the leaf swinging again.
                shouldCollideWithGround=false; // the leaf is back on the tree so the game won't look for
            // collisions between it and the ground.
                leafLifeCycle(); // starts the life cycle all over again.
        };
        new ScheduledTask(
                this, extraDeathTime+BASIC_DEATH_TIME, false,putLeafBackOnTree);
    }
    /*
    determine the leaf's angle and horizontal flipping transitions.
     */
    private void transition(){
        float finalAngle = random.nextBoolean()?FINAL_ANGLE:-FINAL_ANGLE;
        angleTransition = new Transition<Float>(
                this, // the game object being changed
                this.renderer()::setRenderableAngle,// the method to call
                INITIAL_ANGLE, // initial transition value
                finalAngle, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use a linear interpolator
                TRANSITION_CYCLE_LENGTH, // cycle length it takes for the transition to complete.
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null); // nothing further to execute upon reaching final value

        horizontalTransition=new Transition<Vector2>(
                this, // the game object being changed
                this::setDimensions,// the method to call
                INITIAL_DIMENSIONS, // initial transition value
                FINAL_DIMENSIONS, // final transition value
                Transition.LINEAR_INTERPOLATOR_VECTOR, // use a linear interpolator
                TRANSITION_CYCLE_LENGTH, // cycle length it takes for the transition to complete.
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null); // nothing further to execute upon reaching final value
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        // if leaf collides with ground, stop falling and rotating.
        if (other.getTag().equals(Terrain.GROUND_TAG_NAME)){
            this.transform().setVelocityY(0);
            this.removeComponent(horizontalTransition);
            this.removeComponent(angleTransition);

        }
    }
    @Override
    public boolean shouldCollideWith(GameObject other) {
        // determine wether collisions with the ground can happen.
        if (other.getTag().equals(Terrain.GROUND_TAG_NAME)) return shouldCollideWithGround;
        else return super.shouldCollideWith(other);
    }

    /**
     * set the layer in which the leaf is in the game gameObjects collection.
     * @param layer the layer the leaf is in.
     */
    public void setLayer(int layer){
        this.layer = layer;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // remove the leaf from the gameObjects if it is too far from the camera's view.
        DeleteObjectsOutOfScreen.deleteObject(this,layer);
    }

}
