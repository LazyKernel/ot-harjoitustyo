package asteroids.core.game.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera
{
    private static Matrix4f projectionMatrix;
    private static Matrix4f viewMatrix;
    private static Matrix4f projViewMatrix;

    public static void createProjectionMatrix(float width, float height)
    {
        projectionMatrix = new Matrix4f().ortho(0, width, 0, height, 0.01f, 5.0f, false);
        projViewMatrix = null;
    }

    public static void createViewMatrix()
    {
        viewMatrix = new Matrix4f().translate(new Vector3f(0.0f, 0.0f, -1.0f)).lookAlong(new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        projViewMatrix = null;
    }

    public static Matrix4f getProjectionMatrix()
    {
        if (projectionMatrix == null)
        {
            System.err.println("Projection matrix null.");
            return new Matrix4f();
        }

        return projectionMatrix;
    }

    public static Matrix4f getViewMatrix()
    {
        if (viewMatrix == null)
        {
            System.err.println("View matrix null.");
            return new Matrix4f();
        }

        return viewMatrix;
    }

    public static Matrix4f getProjViewMatrix()
    {
        if (projViewMatrix == null)
        {
            projViewMatrix = getProjectionMatrix().mul(getViewMatrix());
        }

        return projViewMatrix;
    }
}
