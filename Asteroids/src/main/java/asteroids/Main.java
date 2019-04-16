package asteroids;

import asteroids.core.networking.ClientNetworking;
import asteroids.core.networking.INetworking;
import asteroids.core.networking.OfflineNetworking;
import asteroids.core.networking.ServerNetworking;
import asteroids.game.Game;
import asteroids.core.graphics.Renderer;

public class Main {
    private Game game = new Game();
    private INetworking networking = null;
    private boolean visualDebug = false;

    public void setArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o")) {
                networking = new OfflineNetworking();
            } else if (args[i].equals("-s")) {
                networking = new ServerNetworking();
            } else if (args[i].equals("-c") && i + 1 < args.length) {
                if (i + 2 < args.length) {
                    networking = new ClientNetworking(args[i + 1], args[i + 2]);
                } else {
                    networking = new ClientNetworking(args[i + 1], "");
                }
            } else if (args[i].equals("-v")) {
                visualDebug = true;
            }
        }

        if (networking == null) {
            networking = new OfflineNetworking();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.setArgs(args);

        Renderer renderer = new Renderer(main.game, main.networking);
        renderer.setIsServerVisualDebug(main.visualDebug);
        renderer.init();
        main.game.init();

        if (main.networking instanceof ClientNetworking) {
            ClientNetworking c = (ClientNetworking) main.networking;
            c.connect();
        }

        renderer.renderLoop();
        renderer.cleanUp();
    }

    // Getters for testing
    public INetworking getNetworking() {
        return networking;
    }

    public boolean isVisualDebug() {
        return visualDebug;
    }
}
