package asteroids;

import asteroids.core.networking.ClientNetworking;
import asteroids.core.networking.INetworking;
import asteroids.core.networking.OfflineNetworking;
import asteroids.core.networking.ServerNetworking;
import asteroids.game.Game;
import asteroids.core.graphics.Renderer;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        INetworking networking = null;
        boolean visualDebug = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o")) {
                networking = new OfflineNetworking();
            } else if (args[i].equals("-s")) {
                networking = new ServerNetworking();
            } else if (args[i].equals("-c") && i + 1 < args.length) {
                if (i+2 < args.length) {
                    networking = new ClientNetworking(args[i+1], args[i+2]);
                } else {
                    networking = new ClientNetworking(args[i+1], "");
                }
            } else if (args[i].equals("-v")) {
                visualDebug = true;
            }
        }

        if (networking == null) {
            networking = new OfflineNetworking();
        }

        Renderer renderer = new Renderer(game, networking);
        renderer.setIsServerVisualDebug(visualDebug);
        renderer.init();
        game.init();

        if (networking instanceof ClientNetworking) {
            ClientNetworking c = (ClientNetworking) networking;
            c.connect();
        }

        renderer.renderLoop();
        renderer.cleanUp();
    }
}
