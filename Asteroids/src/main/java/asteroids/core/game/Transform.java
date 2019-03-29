package asteroids.core.game;

import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform
{
    private Matrix3f transformMatrix;

    private Vector2f position;
    private float rotation;
    private Vector2f scale;

    private boolean updated;

    public Transform()
    {
        transformMatrix = new Matrix3f();
        updated = false;
    }

    public Matrix3f getTransformMatrix() {
        if (updated)
            updateMatrix();

        return transformMatrix;
    }

    public void setTransformMatrix(Matrix3f transformMatrix) {
        this.transformMatrix = transformMatrix;
        updated = false;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
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
        return scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
        updated = true;
    }

    public void translate(Vector2f vec)
    {
        position = new Vector2f(position.x + vec.x, position.y + vec.y);
        updated = true;
    }

    public void translate(Vector3f vec)
    {
        position = new Vector2f(position.x + vec.x, position.y + vec.y);
        updated = true;
    }

    public void rotate(float rotation)
    {
        this.rotation += rotation;
        updated = true;
    }

    public void scale(Vector2f vec)
    {
        scale = new Vector2f(position.x + vec.x, position.y + vec.y);
        updated = true;
    }

    private void updateMatrix()
    {
        this.transformMatrix = new Matrix3f((float)Math.cos(rotation) * scale.x, (float)-Math.sin(rotation), position.x,
                                            (float)Math.sin(rotation), (float)Math.cos(rotation) * scale.y, position.y,
                                            0.0f, 0.0f, 1.0f);
        updated = false;
    }
}
