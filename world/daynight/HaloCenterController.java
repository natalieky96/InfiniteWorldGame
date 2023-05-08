package pepse.world.daynight;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * In charge of updating the center of the HaloSun in each frame to follow the sun.
 */
public class HaloCenterController extends GameObject {
    private final GameObject sun; // the object to update the halo sun accordingly.

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param sun           the object to update the halo sun accordingly.
     */
    public HaloCenterController(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                                GameObject sun) {
        super(topLeftCorner, dimensions, renderable);
        this.sun = sun;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        SunHalo.setHaloCenter(sun.getCenter());
    }
}
