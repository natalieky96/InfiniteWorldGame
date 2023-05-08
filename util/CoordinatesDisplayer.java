package pepse.util;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class CoordinatesDisplayer extends GameObject {
    private final TextRenderable textRenderable;
    private final Camera camera;

    /**
     * Construct a CoordinatesDisplayer GameObject.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public CoordinatesDisplayer(Vector2 topLeftCorner, Vector2 dimensions, Camera camera) {
        super(topLeftCorner, dimensions, null);
        float avatarXCoordinate = camera.getObjectFollowed().getTopLeftCorner().x();
        float avatarYCoordinate =camera.getObjectFollowed().getTopLeftCorner().y();
        textRenderable = new TextRenderable("x: " + avatarXCoordinate + " y:" + avatarYCoordinate);
        textRenderable.setColor(Color.WHITE);
        this.renderer().setRenderable(textRenderable);
        this.camera = camera;
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float avatarXCoordinate = camera.getObjectFollowed().getTopLeftCorner().x();
        float avatarYCoordinate =camera.getObjectFollowed().getTopLeftCorner().y();
        textRenderable.setString("x: " + avatarXCoordinate + " y:" + avatarYCoordinate);

    }
}
