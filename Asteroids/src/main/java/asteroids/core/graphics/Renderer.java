package asteroids.core.graphics;

import asteroids.core.Game;
import asteroids.core.containers.Entity;
import asteroids.core.containers.ModifiableList;
import asteroids.core.graphics.ui.UIManager;
import asteroids.core.networking.ClientNetworking;
import asteroids.core.networking.INetworking;
import asteroids.core.threading.ConsoleThread;
import asteroids.core.input.KeyboardHandler;
import org.joml.Vector2i;
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
    private boolean isServerVisualDebug = false;
    private boolean running = true;
    private int entityIdCounter = 0;
    private UIManager uiManager = null;

    private static Renderer renderer = null;

    public Renderer(Game game, INetworking networking) {
        this.game = game;
        this.game.setRenderer(this);
        this.networking = networking;
        networking.setRenderer(this);
        renderer = this;
    }

    public void init() {
        networking.init();

        if (getIsHeadlessServer() && !getIsServerVisualDebug()) {
            new ConsoleThread(this);
            return;
        }

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

        glfwMakeContextCurrent(pWindow);
        glfwSwapInterval(1);
        glfwShowWindow(pWindow);

        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        if (!getIsHeadlessServer()) {
            uiManager = new UIManager();
            uiManager.loadFont("src/main/resources/Roboto-Regular.ttf");
            uiManager.init(pWindow);
            uiManager.updateScreenDimensions(800, 600);
        }

        glfwSetKeyCallback(pWindow, keyCallback = new KeyboardHandler(uiManager));
    }

    public void renderLoop() {
        if (getIsHeadlessServer() && !getIsServerVisualDebug()) {
            renderLoopServer();
            return;
        }

        long lastTime = System.nanoTime();
        final float divisor = 1000000000.0f;
        while (!glfwWindowShouldClose(pWindow)) {
            if (!getIsServerVisualDebug()) {
                uiManager.beginInput();
            }

            glfwPollEvents();

            if (!getIsServerVisualDebug()) {
                uiManager.endInput(pWindow);
            }

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

            if (!getIsServerVisualDebug()) {
                uiManager.render();
            }

            glfwSwapBuffers(pWindow);
        }
    }

    public void renderLoopServer() {
        long lastTime = System.nanoTime();
        final float divisor = 1000000000.0f;
        while (running) {
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

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted.");
            }
        }
    }

    public void cleanUp() {
        networking.destroy();
        game.destroy();
        destroyEntities();

        if (getIsHeadlessServer() && !getIsServerVisualDebug()) {
            return;
        }

        glfwFreeCallbacks(pWindow);
        glfwDestroyWindow(pWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void destroyEntities() {
        for (Entity e : entities) {
            if (e == null) {
                continue;
            }

            e.destroy();
        }

        entities.clear();
    }

    public void connect(String name, String ip) {
        networking.destroy();
        destroyEntities();

        ClientNetworking net = new ClientNetworking(ip, name);
        net.setRenderer(this);
        net.init();
        net.connect();
        networking = net;
    }

    public void addEntity(Entity entity) {
        if (getIsServer()) {
            entity.setEntityId(entityIdCounter++);
        }

        entities.add(entity);
    }

    public Entity getEntity(int entityId) {
        return entities.getWithEntityId(entityId);
    }

    public void removeEntity(Entity entity) {
        if (entity == null) {
            return;
        }

        removeEntity(entity.getEntityId());
    }

    public void removeEntity(int entityId) {
        Entity e = entities.getWithEntityId(entityId);

        if (e != null) {
            e.destroy();
        }

        entities.remove(entityId);
    }

    public Game getGame() {
        return game;
    }

    public boolean getIsServer() {
        return networking.getIsServer();
    }

    public boolean getIsOffline() {
        return networking.isOffline();
    }

    public boolean getIsHeadlessServer() {
        return getIsServer() && !getIsOffline();
    }

    public boolean getIsServerVisualDebug() {
        return isServerVisualDebug;
    }

    public void setIsServerVisualDebug(boolean value) {
        this.isServerVisualDebug = value;
    }

    public INetworking getNetworking() {
        return networking;
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public void quit() {
        if (getIsHeadlessServer()) {
            this.running = false;
        } else {
            glfwSetWindowShouldClose(pWindow, true);
        }
    }

    public UIManager getUiManager() {
        return uiManager;
    }

    public Vector2i getWindowSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(pWindow, pWidth, pHeight);

            return new Vector2i(pWidth.get(0), pHeight.get(0));
        } catch (Exception e) {
            System.err.println("An exception occurred while getting window size.\n" + e);
            return new Vector2i(-1, -1);
        }
    }
}
