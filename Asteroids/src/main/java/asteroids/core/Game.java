package asteroids.core;

import asteroids.core.graphics.Renderer;

public abstract class Game {
    protected Renderer renderer;

    public abstract void init();
    public abstract void update();
    public abstract void render();
    public abstract void destroy();

    public abstract void connected();
    public abstract void disconnected();

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
}
