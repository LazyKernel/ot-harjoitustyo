package asteroids.core.containers;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform {
    private Matrix4f transformMatrix;

    private Vector3f position;
    private float rotation;
    private Vector3f scale;

    private boolean updated;

    private final float referenceX = 800.0f;
    private final float referenceY = 600.0f;

    public Transform() {
        transformMatrix = new Matrix4f();
        position = new Vector3f(0.0f, 0.0f, 1.0f);
        rotation = 0.0f;
        scale = new Vector3f(100.0f / referenceX, 100.0f / referenceY, 1.0f);
        updated = false;
    }

    public Matrix4f getTransformMatrix() {
        if (updated) {
            updateMatrix();
        }

        return transformMatrix;
    }

    public void setTransformMatrix(Matrix4f transformMatrix) {
        this.transformMatrix = transformMatrix;
        updated = false;
    }

    public Vector2f getPosition() {
        return new Vector2f(position.x * referenceX, position.y * referenceY);
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position.x / referenceX, position.y / referenceY, 1.0f);
        updated = true;
    }

    public void setPosition(Vector2f position) {
        this.position = new Vector3f(position.x / referenceX, position.y / referenceY, 1.0f);
        updated = true;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        updated = true;
    }

    public Vector2f getScale() {
        return new Vector2f(scale.x * referenceX, scale.y * referenceY);
    }

    public void setScale(Vector2f scale) {
        this.scale = new Vector3f(scale.x / referenceX, scale.y / referenceY, 1.0f);
        updated = true;
    }

    public void translate(Vector2f vec) {
        position = new Vector3f(position.x + vec.x / referenceX, position.y + vec.y / referenceY, 1.0f);
        updated = true;
    }

    public void translate(Vector3f vec) {
        position = new Vector3f(position.x + vec.x / referenceX, position.y + vec.y / referenceY, 1.0f);
        updated = true;
    }

    public void rotate(float rotation) {
        this.rotation += rotation;
        updated = true;
    }

    public void scale(Vector2f vec) {
        scale = new Vector3f(position.x + vec.x / referenceX, position.y + vec.y / referenceY, 1.0f);
        updated = true;
    }

    private void updateMatrix() {
        this.transformMatrix = new Matrix4f().translate(position).rotateZ(rotation).scale(scale);
        updated = false;
    }
}
