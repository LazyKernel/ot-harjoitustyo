package asteroids.game.ui;

import asteroids.core.containers.Transform;
import asteroids.core.graphics.Renderer;
import asteroids.core.graphics.ui.UIManager;
import asteroids.core.graphics.ui.elements.DynamicRow;
import asteroids.core.graphics.ui.elements.Label;
import asteroids.core.graphics.ui.elements.Window;
import asteroids.core.graphics.ui.elements.callbacks.ICursorCallback;
import asteroids.game.components.Player;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;

import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_LEFT;

/**
 * Sliding score window
 */
public class ScoreWindow implements ICursorCallback {

    private Renderer renderer = null;

    private Window window = null;
    private Label[] names;
    private Label[] scores;

    private boolean showWindow = false;

    private ArrayList<Player> players = new ArrayList<>();

    private Vector2i windowSize = new Vector2i();

    /**
     * Creates the display and elements
     * @param renderer current renderer
     */
    public void createMenu(Renderer renderer) {
        this.renderer = renderer;

        UIManager ui = renderer.getUiManager();

        window = new Window("", false, false);
        DynamicRow row = new DynamicRow(30, 1);

        names = new Label[4];
        scores = new Label[4];

        Transform t = window.getTransform();
        windowSize = renderer.getWindowSize();

        if (windowSize.x < 0 || windowSize.y < 0) {
            System.err.println("Window dimensions are less than 0.");
            return;
        }

        t.setScale(new Vector2f(200, windowSize.y));
        t.setPosition(new Vector2f(windowSize.x, 0));

        ui.addElement(window);
        window.addElement(row);

        for (int i = 0; i < 4; i++) {
            names[i] = new Label("", NK_TEXT_ALIGN_LEFT);
            scores[i] = new Label("", NK_TEXT_ALIGN_LEFT);
            row.addElement(new Label("", NK_TEXT_ALIGN_LEFT));
            row.addElement(names[i]);
            row.addElement(scores[i]);
        }

        ui.addCursorCallback(this);
    }

    /**
     * Animates the sliding display
     * @param deltaTime seconds since last update
     */
    public void update(float deltaTime) {
        if (window == null || renderer.getIsOffline()) {
            return;
        }

        Vector2f pos = window.getTransform().getPosition();
        if (showWindow && pos.x > windowSize.x - 200) {
            pos.x -= deltaTime * 400;
        }

        if (!showWindow && pos.x < windowSize.x) {
            pos.x += deltaTime * 400;
        }

        if (pos.x > windowSize.x) {
            pos.x = windowSize.x;
        } else if (pos.x < windowSize.x - 200) {
            pos.x = windowSize.x - 200;
        }

        window.getTransform().setPosition(pos);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(player)) {
                updateLabel(i, "", "");
                players.remove(i);
                return;
            }
        }
    }

    /**
     * Update player score and name
     * @param player player to update
     */
    public void updatePlayer(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(player)) {
                updateLabel(i, player.getOwner(), Integer.toString(player.getScore()));
                return;
            }
        }
    }

    private void updateLabel(int idx, String name, String score) {
        if (idx > 3 || idx < 0) {
            return;
        }

        if (names != null && names[idx] != null && scores != null && scores[idx] != null) {
            names[idx].setText(name);
            scores[idx].setText(score);
        }
    }

    /**
     * Removes this from ui manager and removes the cursor callback
     */
    public void destroy() {
        if (window != null && renderer != null) {
            renderer.getUiManager().removeElement(window);
            renderer.getUiManager().removeCursorCallback(this);
            window = null;
            names = null;
            scores = null;
        }
    }

    /**
     * Implements cursor pos callback
     * @param x cursor x
     * @param y cursor y
     */
    @Override
    public void cursorPos(int x, int y) {
        if (x >= windowSize.x - 20 && !showWindow) {
            showWindow = true;
        }

        if (x < windowSize.x - 200 && showWindow) {
            showWindow = false;
        }
    }
}
