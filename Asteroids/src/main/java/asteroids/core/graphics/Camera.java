package asteroids.core.graphics;

import org.joml.Matrix4f;

public class Camera
{
    private static Matrix4f projectionMatrix;

    public static void createProjectionMatrix(float width, float height)
    {
        projectionMatrix = new Matrix4f().ortho(0, width, 0, height, 0.01f, 5.0f, false);
    }

    public static Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }
}
