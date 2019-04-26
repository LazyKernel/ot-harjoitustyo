package asteroids.core.graphics.ui.elements;

import asteroids.core.graphics.ui.UIElement;
import asteroids.core.graphics.ui.elements.callbacks.IButtonCallback;

import static org.lwjgl.nuklear.Nuklear.nk_button_label;

public class Button extends UIElement {
    private String text;
    private IButtonCallback listener;
    private Object userData;

    public Button() {
        text = "";
        listener = null;
    }

    public Button(String text, IButtonCallback listener, Object userData) {
        this.text = text;
        this.listener = listener;
        this.userData = userData;
    }

    public void init() {

    }

    public void render() {
        if (getCtx() == null) {
            return;
        }

        if (nk_button_label(getCtx(), text)) {
            if (listener != null) {
                listener.buttonClicked(userData);
            }
        }
    }

    public void update(float deltaTime) {

    }

    public void destroy() {

    }

    @Override
    public void addElement(UIElement element) {
        System.err.println("Button cannot have children.");
    }

    @Override
    public void removeElement(UIElement element) {
        System.err.println("Button cannot have children.");
    }
}
