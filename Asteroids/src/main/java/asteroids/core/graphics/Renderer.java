package asteroids.core.graphics;

import asteroids.core.containers.Entity;
import asteroids.core.containers.ModifiableList;
import asteroids.core.networking.INetworking;
import asteroids.game.Game;
import asteroids.core.input.KeyboardHandler;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {
    private long pWindow = 0;
    private ModifiableList<Entity> entities = new ModifiableList<>();
    private Game game;
    private GLFWKeyCallback keyCallback;
    private INetworking networking;

    private static Renderer renderer = null;

    public Renderer(Game game, INetworking networking) {
        this.game = game;
        this.game.setRenderer(this);
        this.networking = networking;
    }

    public void init() {
        renderer = this;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            System.err.println("Unable to initialize GLFW.");
            return;
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // keep the window hidden till we're done moving and setting it up
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 4); // 4x MSAA

        pWindow = glfwCreateWindow(800, 600, "Asteroids", NULL, NULL);
        if (pWindow == NULL) {
            System.err.println("Failed to create a GLFW window.");
            return;
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(pWindow, pWidth, pHeight);

            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(pWindow, (videoMode.width() - pWidth.get(0)) / 2, (videoMode.height() - pHeight.get(0)) / 2);
        }

        glfwSetKeyCallback(pWindow, keyCallback = new KeyboardHandler());

        glfwMakeContextCurrent(pWindow);
        glfwSwapInterval(1);
        glfwShowWindow(pWindow);

        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void renderLoop() {
        long lastTime = System.nanoTime();
        final float divisor = 1000000000.0f;
        while (!glfwWindowShouldClose(pWindow)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float delta = (System.nanoTime() - lastTime) / divisor;
            networking.preUpdate(delta);
            game.update();
            for (Entity e : entities) {
                if (e == null) {
                    continue;
                }

                e.update(delta);
            }
            networking.postUpdate(delta);
            lastTime = System.nanoTime();

            game.render();
            for (Entity e : entities) {
                if (e == null) {
                    continue;
                }

                e.render();
            }

            glfwSwapBuffers(pWindow);
            glfwPollEvents();
        }
    }

    public void cleanUp() {
        game.destroy();

        for (Entity e : entities) {
            if (e == null) {
                continue;
            }

            e.destroy();
        }

        glfwFreeCallbacks(pWindow);
        glfwDestroyWindow(pWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        removeEntity(entity.getEntityId());
    }

    private void removeEntity(int entityId) {
        Entity e = entities.get(entityId);

        if (e != null) {
            e.destroy();
        }

        entities.remove(entityId);
    }

    public boolean getIsServer() {
        return networking.getIsServer();
    }

    public INetworking getNetworking() {
        return networking;
    }

    public static Renderer getRenderer() {
        return renderer;
    }
}
