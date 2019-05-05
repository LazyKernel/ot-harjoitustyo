package asteroids.core.input;

import asteroids.core.graphics.ui.UIManager;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * Handles keyboard input
 */
public class KeyboardHandler extends GLFWKeyCallback {
    private static boolean[] keysHold = new boolean[65536];
    private static boolean[] keysPress = new boolean[65536];

    private static boolean blockInput = false;

    private UIManager uiManager;

    public KeyboardHandler(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    /**
     * Called by glfw when it gets a key input
     * @param pWindow pointer to glfw window
     * @param key pressed key
     * @param scanCode no clue
     * @param action was the key pressed or not
     * @param modifiers were any modifiers pressed down when the key was pressed
     */
    @Override
    public void invoke(long pWindow, int key, int scanCode, int action, int modifiers) {
        if (!blockInput) {
            keysPress[key] = action == GLFW_PRESS;
            keysHold[key] = action != GLFW_RELEASE;
        }

        if (uiManager != null) {
            uiManager.keyCallback(pWindow, key, action);
        }
    }

    public static boolean isKeyDown(int key) {
        return keysHold[key];
    }

    public static boolean isKeyPressed(int key) {
        boolean ret = keysPress[key];
        keysPress[key] = false;
        return ret;
    }

    /**
     * Set ui blocking input
     * @param blockInput true if it blocks input
     */
    public static void setBlockInput(boolean blockInput) {
        KeyboardHandler.blockInput = blockInput;
    }
}
