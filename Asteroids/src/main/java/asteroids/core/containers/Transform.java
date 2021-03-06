package asteroids.core.containers;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Automatically converts world coordinates [-800, -600] - [800, 600] to unit coordinates
 */
public class Transform {

    private Vector3f position;
    private float rotation;
    private Vector3f scale;

    private final float referenceX = 800.0f;
    private final float referenceY = 600.0f;

    /**
     * Basic transform at origin with no rotation and 100 scale on both axes
     */
    public Transform() {
        position = new Vector3f(0.0f, 0.0f, 1.0f);
        rotation = 0.0f;
        scale = new Vector3f(100.0f / referenceX, 100.0f / referenceY, 1.0f);
    }

    /**
     * Returns unit transformation matrix
     * @return transformation matrix
     */
    public Matrix4f getTransformMatrix() {
        return new Matrix4f().translate(position).rotateZ(rotation).scale(scale);
    }

    /**
     * Return world transformation matrix
     * @return transformation matrix
     */
    public Matrix4f getScaledTransformMatrix() {
        return new Matrix4f().translate(getPosition3d()).rotateZ(rotation).scale(getScale3d());
    }

    public Vector2f getPosition() {
        return new Vector2f(position.x * referenceX, position.y * referenceY);
    }

    public Vector3f getPosition3d() {
        return new Vector3f(position.x * referenceX, position.y * referenceY, 0.0f);
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position.x / referenceX, position.y / referenceY, 1.0f);
    }

    public void setPosition(Vector2f position) {
        this.position = new Vector3f(position.x / referenceX, position.y / referenceY, 1.0f);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Vector2f getScale() {
        return new Vector2f(scale.x * referenceX, scale.y * referenceY);
    }

    public Vector3f getScale3d() {
        return new Vector3f(scale.x * referenceX, scale.y * referenceY, 1.0f);
    }

    public void setScale(Vector2f scale) {
        this.scale = new Vector3f(scale.x / referenceX, scale.y / referenceY, 1.0f);
    }

    public void translate(Vector2f vec) {
        position = new Vector3f(position.x + vec.x / referenceX, position.y + vec.y / referenceY, 1.0f);
    }

    public void translate(Vector3f vec) {
        position = new Vector3f(position.x + vec.x / referenceX, position.y + vec.y / referenceY, 1.0f);
    }

    public void rotate(float rotation) {
        this.rotation += rotation;
    }
}
