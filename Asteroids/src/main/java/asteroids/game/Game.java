package asteroids.game;

import asteroids.core.containers.Entity;
import asteroids.core.graphics.ui.UIManager;
import asteroids.core.graphics.ui.elements.Window;
import asteroids.game.components.Bullet;
import asteroids.game.components.Player;
import asteroids.core.graphics.Renderer;

public class Game {
    private Renderer renderer;

    public void init() {
        if (!renderer.getIsOffline()) {
            renderer.getNetworking().registerClass(Player.class);
            renderer.getNetworking().registerClass(Bullet.class);
        }

        if (renderer.getIsOffline()) {
            Entity player = new Entity();
            Player playerComponent = new Player();
            player.addComponent(playerComponent);
            renderer.addEntity(player);
        }

        Window window = new Window("Test", true);
        renderer.getUiManager().addElement(window);
    }

    public void update() {

    }

    public void render() {

    }

    public void destroy() {

    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
}
