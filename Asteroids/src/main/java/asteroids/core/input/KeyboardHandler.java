package asteroids.core.game.input;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyboardHandler extends GLFWKeyCallback
{
    public static boolean[] keys = new boolean[65536];

    @Override
    public void invoke(long pWindow, int key, int scanCode, int action, int modifiers)
    {
        keys[key] = action != GLFW_RELEASE;
    }

    public static boolean isKeyDown(int key)
    {
        return keys[key];
    }
}
