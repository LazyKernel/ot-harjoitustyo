package asteroids.core.graphics;

import asteroids.core.game.Entity;
import asteroids.core.game.Game;
import asteroids.core.game.input.KeyboardHandler;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer
{
    private long pWindow = 0;
    private List<Entity> entities = new ArrayList<>();
    private Game game;
    private GLFWKeyCallback keyCallback;

    public Renderer(Game game)
    {
        this.game = game;
        this.game.setRenderer(this);
    }

    public void init()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
        {
            System.err.println("Unable to initialize GLFW.");
            return;
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // keep the window hidden till we're done moving and setting it up
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        pWindow = glfwCreateWindow(800, 600, "Asteroids", NULL, NULL);
        if (pWindow == NULL)
        {
            System.err.println("Failed to create a GLFW window.");
            return;
        }

        Camera.createProjectionMatrix(1, 1);
        Camera.createViewMatrix();

        try (MemoryStack stack = stackPush())
        {
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

    public void renderLoop()
    {
        long lastTime = System.nanoTime();
        final float divisor = 1000000000.0f;
        while (!glfwWindowShouldClose(pWindow))
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            game.update();
            for (Entity e : entities)
            {
                float delta = (System.nanoTime() - lastTime) / divisor;
                e.update(delta);
                lastTime = System.nanoTime();
            }

            game.render();
            for (Entity e : entities)
            {
                e.render();
            }

            glfwSwapBuffers(pWindow);
            glfwPollEvents();
        }
    }

    public void cleanUp()
    {
        game.destroy();

        for (Entity e : entities)
        {
            e.destroy();
        }

        glfwFreeCallbacks(pWindow);
        glfwDestroyWindow(pWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void addEntity(Entity entity)
    {
        entities.add(entity);
    }
}
