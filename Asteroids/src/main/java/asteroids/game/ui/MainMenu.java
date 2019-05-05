package asteroids.game.ui;

import asteroids.core.containers.Transform;
import asteroids.core.graphics.Renderer;
import asteroids.core.graphics.ui.UIManager;
import asteroids.core.graphics.ui.elements.*;
import asteroids.core.graphics.ui.elements.callbacks.IButtonCallback;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.nuklear.Nuklear;

import java.nio.charset.Charset;

import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_BOTTOM;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_LEFT;

/**
 * Main menu ui
 */
public class MainMenu implements IButtonCallback {

    private Window window = null;
    private TextField nameField = null;
    private TextField ipField = null;
    private Renderer renderer = null;

    /**
     * Creates the main menu
     * @param renderer current renderer
     */
    public void createMenu(Renderer renderer) {
        this.renderer = renderer;

        UIManager ui = renderer.getUiManager();

        window = new Window("Asteroids", true, true);
        Label nameLabel = new Label("Username", NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_BOTTOM);
        nameField = new TextField(12, false, Nuklear::nnk_filter_ascii, Charset.forName("US-ASCII"));
        Label ipLabel = new Label("IP", NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_BOTTOM);
        ipField = new TextField("okay.works", 50, false, Nuklear::nnk_filter_ascii, Charset.forName("US-ASCII"));
        Button connectButton = new Button("Connect", this, "connect");
        Label emptyLabel = new Label("", NK_TEXT_ALIGN_LEFT);
        Button spectateButton = new Button("Spectate", this, "spectate");
        DynamicRow row = new DynamicRow(30, 1);

        Transform t = window.getTransform();
        t.setScale(new Vector2f(300, 300));
        Vector2i windowSize = renderer.getWindowSize();

        if (windowSize.x < 0 || windowSize.y < 0) {
            System.err.println("Window dimensions are less than 0.");
            return;
        }

        t.setPosition(new Vector2f((windowSize.x / 2.0f) - 150.0f, (windowSize.y / 2.0f) - 185.0f));

        ui.addElement(window);
        window.addElement(row);
        row.addElement(nameLabel);
        row.addElement(nameField);
        row.addElement(ipLabel);
        row.addElement(ipField);
        row.addElement(connectButton);
        row.addElement(emptyLabel);
        row.addElement(spectateButton);
    }

    /**
     * Called when a button was clicked
     * @param userData string to determine which button was clicked
     */
    @Override
    public void buttonClicked(Object userData) {
        if (userData instanceof String) {
            String button = (String) userData;

            if (button.equals("connect")) {
                if (ipField == null) {
                    System.err.println("IpField was null.");
                    return;
                }

                if (nameField == null) {
                    System.err.println("NameField was null.");
                    return;
                }

                if (renderer == null) {
                    System.err.println("Renderer was null.");
                    return;
                }

                String name = nameField.getValue();
                String ip = ipField.getValue();
                renderer.connect(name, ip);
                destroy();
            } else if (button.equals("spectate")) {
                if (ipField == null) {
                    System.err.println("IpField was null.");
                    return;
                }

                if (renderer == null) {
                    System.err.println("Renderer was null.");
                    return;
                }

                String ip = ipField.getValue();
                renderer.connect("", ip);
                destroy();
            }
        }
    }

    /**
     * Removes the window from ui manager
     */
    public void destroy() {
        if (window != null && renderer != null) {
            renderer.getUiManager().removeElement(window);
            window = null;
            nameField = null;
            ipField = null;
        }
    }

    public Window getWindow() {
        return window;
    }
}
