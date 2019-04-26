package asteroids.game;

import asteroids.core.Game;
import asteroids.core.containers.Entity;
import asteroids.game.components.Bullet;
import asteroids.game.components.Player;


public class AsteroidsGame extends Game {

    MainMenu menu = null;

    @Override
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

            menu = new MainMenu();
            menu.createMenu(renderer);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void connected() {
        if (menu != null) {
            menu.destroy();
        }
    }

    @Override
    public void disconnected() {
        if (menu != null) {
            menu.createMenu(renderer);
        }
    }
}
