package asteroids.core.input;

import asteroids.core.graphics.ui.UIManager;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyboardHandler extends GLFWKeyCallback {
    private static boolean[] keysHold = new boolean[65536];
    private static boolean[] keysPress = new boolean[65536];

    private UIManager uiManager;

    public KeyboardHandler(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    @Override
    public void invoke(long pWindow, int key, int scanCode, int action, int modifiers) {
        keysPress[key] = action == GLFW_PRESS;
        keysHold[key] = action != GLFW_RELEASE;
        uiManager.keyCallback(pWindow, key, action);
    }

    public static boolean isKeyDown(int key) {
        return keysHold[key];
    }

    public static boolean isKeyPressed(int key) {
        boolean ret = keysPress[key];
        keysPress[key] = false;
        return ret;
    }
}
