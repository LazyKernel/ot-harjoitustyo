package asteroids.core;

import asteroids.core.graphics.Renderer;

/**
 * Base class for the game
 */
public abstract class Game {
    protected Renderer renderer;

    /**
     * Ran when the game starts
     */
    public abstract void init();

    /**
     * Ran every frame before rendering
     * @param deltaTime seconds since last update
     */
    public abstract void update(float deltaTime);

    /**
     * Ran after update
     */
    public abstract void render();

    /**
     * Ran when the game shuts down
     */
    public abstract void destroy();

    /**
     * Called when connected to the game
     */
    public abstract void connected();

    /**
     * Called when disconnected from a server
     */
    public abstract void disconnected();

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
}
