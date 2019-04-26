package asteroids.core.graphics.ui.elements;

import asteroids.core.graphics.ui.UIElement;

import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_LEFT;
import static org.lwjgl.nuklear.Nuklear.nk_label;

public class Label extends UIElement {

    private String text;
    private int flags;

    public Label() {
        text = "Label";
        flags = NK_TEXT_ALIGN_LEFT;
    }

    public Label(String text, int flags) {
        this.text = text;
        this.flags = flags;
    }

    public void init() {

    }

    public void render() {
        if (getCtx() == null) {
            return;
        }

        nk_label(getCtx(), text, flags);
    }

    public void update(float deltaTime) {

    }

    public void destroy() {

    }

    @Override
    public void addElement(UIElement element) {
        System.err.println("Label cannot have children.");
    }

    @Override
    public void removeElement(UIElement element) {
        System.err.println("Label cannot have children.");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
