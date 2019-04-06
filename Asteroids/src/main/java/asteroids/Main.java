package asteroids;

import asteroids.game.Game;
import asteroids.core.graphics.Renderer;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        Renderer renderer = new Renderer(game);
        renderer.init();
        game.init();
        renderer.renderLoop();
        renderer.cleanUp();
    }
}
