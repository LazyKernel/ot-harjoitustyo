package asteroids.core.game.components;

import asteroids.core.game.EntityComponent;
import asteroids.core.game.input.KeyboardHandler;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class Player extends EntityComponent
{

    @Override
    public void init()
    {

    }

    @Override
    public void render()
    {

    }

    @Override
    public void update(float deltaTime)
    {
        if (KeyboardHandler.isKeyDown(GLFW_KEY_A))
        {
            getTransform().rotate(-(float)Math.PI * deltaTime);
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_D))
        {
            getTransform().rotate((float)Math.PI * deltaTime);
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_W))
        {
            Vector3f dir = new Vector3f(0.0f, 1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0, 0, 1).transform(dir);
            getTransform().translate(dir.mul(deltaTime * 400));
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_S))
        {
            Vector3f dir = new Vector3f(0.0f, -1.0f, 0.0f);
            new Matrix3f().rotate(getTransform().getRotation(), 0,0, 1).transform(dir);
            getTransform().translate(dir.mul(deltaTime * 400));
        }
    }

    @Override
    public void destroy()
    {

    }
}
