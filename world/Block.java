package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.DeleteObjectsOutOfScreen;
import pepse.world.trees.Tree;


/**
 * Represents an unmovable block.
 */
public class Block extends GameObject {
    public static final int SIZE = 30;
    private int layer;

    public Block(Vector2 topLeftCorner, Renderable renderable){
        super(topLeftCorner, Vector2.ONES.mult(SIZE),renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
    public void setLayer(int layer){
        this.layer = layer;
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (other.getTag().equals(Tree.LEAF_TAG)) return true;
        else return super.shouldCollideWith(other);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        DeleteObjectsOutOfScreen.deleteObject(this,layer);
    }
}
