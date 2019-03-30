package asteroids.core.game;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform
{
    private Matrix4f transformMatrix;

    private Vector3f position;
    private float rotation;
    private Vector3f scale;

    private boolean updated;

    public Transform()
    {
        transformMatrix = new Matrix4f();
        position = new Vector3f(0.0f, 0.0f, 1.0f);
        rotation = 0.0f;
        scale = new Vector3f(1.0f, 1.0f, 1.0f);
        updated = false;
    }

    public Matrix4f getTransformMatrix() {
        if (updated)
            updateMatrix();

        return transformMatrix;
    }

    public void setTransformMatrix(Matrix4f transformMatrix) {
        this.transformMatrix = transformMatrix;
        updated = false;
    }

    public Vector2f getPosition() {
        return new Vector2f(position.x, position.y);
    }

    public void setPosition(Vector2f position) {
        this.position = new Vector3f(position.x, position.y, 1.0f);
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
        return new Vector2f(scale.x, scale.y);
    }

    public void setScale(Vector2f scale) {
        this.scale = new Vector3f(scale.x, scale.y, 1.0f);
        updated = true;
    }

    public void translate(Vector2f vec)
    {
        position = new Vector3f(position.x + vec.x, position.y + vec.y, 1.0f);
        updated = true;
    }

    public void translate(Vector3f vec)
    {
        position = new Vector3f(position.x + vec.x, position.y + vec.y, 1.0f);
        updated = true;
    }

    public void rotate(float rotation)
    {
        this.rotation += rotation;
        updated = true;
    }

    public void scale(Vector2f vec)
    {
        scale = new Vector3f(position.x + vec.x, position.y + vec.y, 1.0f);
        updated = true;
    }

    private void updateMatrix()
    {
        /*this.transformMatrix = new Matrix4f((float)Math.cos(rotation) * scale.x, (float)-Math.sin(rotation), position.x, 0.0f,
                                            (float)Math.sin(rotation), (float)Math.cos(rotation) * scale.y, position.y, 0.0f,
                                            0.0f, 0.0f, 1.0f, 0.0f,
                                            0.0f, 0.0f, 0.0f, 1.0f);*/
        this.transformMatrix = new Matrix4f().scale(scale).rotateZ(rotation).translate(position);
        updated = false;
    }
}
