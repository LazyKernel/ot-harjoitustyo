package asteroids;

import asteroids.core.networking.INetworking;
import asteroids.core.networking.OfflineNetworking;
import asteroids.game.Game;
import asteroids.core.graphics.Renderer;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        INetworking networking = new OfflineNetworking();
        Renderer renderer = new Renderer(game, networking);
        renderer.init();
        game.init();
        renderer.renderLoop();
        renderer.cleanUp();
    }
}
