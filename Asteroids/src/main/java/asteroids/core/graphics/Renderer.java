package asteroids.core.graphics;

import asteroids.core.Game;
import asteroids.core.containers.Entity;
import asteroids.core.containers.ModifiableList;
import asteroids.core.graphics.ui.UIManager;
import asteroids.core.networking.ClientNetworking;
import asteroids.core.networking.INetworking;
import asteroids.core.physics.PhysicsEngine;
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

/**
 * Main engine for the game. Handles pretty much everything
 */
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
    private PhysicsEngine physics;

    private static Renderer renderer = null;

    public Renderer(Game game, INetworking networking) {
        this.game = game;
        this.game.setRenderer(this);
        this.networking = networking;
        networking.setRenderer(this);
        renderer = this;
        physics = new PhysicsEngine();
    }

    /**
     * Initializes all other engines and the main window or console thread if this is a headless server
     */
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

    /**
     * Main render loop for the game. If this is a headless server it switches to using server render loop
     * @see Renderer#renderLoopServer()
     */
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
            game.update(delta);
            for (Entity e : entities) {
                if (e == null) {
                    continue;
                }

                e.update(delta);
            }

            physics.update(delta, getIsServer());

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

    /**
     * Render loop for the headless server
     */
    public void renderLoopServer() {
        long lastTime = System.nanoTime();
        final float divisor = 1000000000.0f;
        while (running) {
            float delta = (System.nanoTime() - lastTime) / divisor;
            networking.preUpdate(delta);
            game.update(delta);
            for (Entity e : entities) {
                if (e == null) {
                    continue;
                }

                e.update(delta);
            }

            physics.update(delta, true);

            networking.postUpdate(delta);
            lastTime = System.nanoTime();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted.");
            }
        }
    }

    /**
     * Clean up all entities, engines and the game
     */
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

    /**
     * Connect to a server
     * @param name user name
     * @param ip ip of the server
     */
    public void connect(String name, String ip) {
        networking.destroy();
        destroyEntities();

        ClientNetworking net = new ClientNetworking(ip, name);
        net.setRenderer(this);
        net.init();
        net.connect();
        networking = net;
    }

    /**
     * Add a new entity. If this is a server, also gives it an id that's replicated over the net
     * @param entity new entity to add
     */
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

    public PhysicsEngine getPhysics() {
        return physics;
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    /**
     * Close the game or server
     */
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

    /**
     * Returns the current size of the window
     * @return 2d int vector containing width in x and height in y
     */
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
