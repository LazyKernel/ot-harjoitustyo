package asteroids.game;

import asteroids.core.Game;
import asteroids.core.containers.Entity;
import asteroids.core.containers.Transform;
import asteroids.core.graphics.ui.elements.callbacks.IWindowCallback;
import asteroids.game.components.Asteroid;
import asteroids.game.components.Bullet;
import asteroids.game.components.Player;
import asteroids.game.ui.MainMenu;
import asteroids.game.ui.ScoreWindow;
import org.joml.Vector2f;

import java.util.Random;

/**
 * Game implementation for asteroids
 */
public class AsteroidsGame extends Game implements IWindowCallback {

    private MainMenu menu = null;
    private ScoreWindow score = null;

    private Random rand = new Random();

    private boolean mainMenuClosed = true;

    /**
     * Creates menus and registers classes in networking specific to this game. Creates a local player.
     */
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
            menu.getWindow().setCallback(this);
            mainMenuClosed = false;
        }

        if (!renderer.getIsHeadlessServer()) {
            score = new ScoreWindow();

            if (mainMenuClosed) {
                score.createMenu(renderer);
            }
        }
    }

    /**
     * If this is a server, creates asteroids randomly. Also updates score display on clients.
     * @param deltaTime seconds since last update
     */
    @Override
    public void update(float deltaTime) {
        if (renderer.getIsServer() && mainMenuClosed && rand.nextFloat() < 0.002f) {
            createAsteroid();
        }

        if (score != null) {
            score.update(deltaTime);
        }
    }

    /**
     * Create a new asteroid
     */
    public void createAsteroid() {
        Entity entity = new Entity();
        renderer.addEntity(entity);
        Asteroid asteroid = new Asteroid();
        entity.addComponent(asteroid);
        Transform t = getRandomTransform();
        entity.getTransform().setPosition(t.getPosition());
        entity.getTransform().setRotation(t.getRotation());
    }

    private Transform getRandomTransform() {
        int randomSide = rand.nextInt(4);
        Vector2f pos;
        float rotation = rand.nextFloat() * (float) Math.PI / 2.0f;

        if (randomSide == 0) {
            int randomPos = rand.nextInt(1800) - 900;
            pos = new Vector2f(randomPos, 700);
            rotation -= 3.0f * (float) Math.PI / 4.0f;
        } else if (randomSide == 1) {
            int randomPos = rand.nextInt(1400) - 700;
            pos = new Vector2f(900, randomPos);
            rotation += 3.0f * (float) Math.PI / 4.0f;
        } else if (randomSide == 2) {
            int randomPos = rand.nextInt(1800) - 900;
            pos = new Vector2f(randomPos, -700);
            rotation += (float) Math.PI / 4.0f;
        } else {
            int randomPos = rand.nextInt(1400) - 700;
            pos = new Vector2f(-900, randomPos);
            rotation -= (float) Math.PI / 4.0f;
        }

        Transform t = new Transform();
        t.setPosition(pos);
        t.setRotation(rotation);
        return t;
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

        if (score != null) {
            score.createMenu(renderer);
        }
    }

    @Override
    public void disconnected() {
        if (menu != null) {
            menu.createMenu(renderer);
        }

        if (score != null) {
            score.destroy();
        }
    }

    public ScoreWindow getScoreWindow() {
        return score;
    }

    @Override
    public void windowClosed(String title) {
        if (title.equals("Asteroids")) {
            mainMenuClosed = true;
            score.createMenu(renderer);
        }
    }
}
