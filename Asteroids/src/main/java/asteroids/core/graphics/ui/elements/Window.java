package asteroids.core.graphics.ui.elements;

import asteroids.core.containers.Transform;
import asteroids.core.graphics.ui.UIElement;
import org.joml.Vector2f;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

public class Window extends UIElement {
    private String name;
    private boolean closeable;
    private int flags;
    private NkRect windowRect = null;

    public Window() {
        name = "";
        closeable = true;
        updateFlags();
    }

    public Window(String name, boolean closeable) {
        this.name = name;
        this.closeable = closeable;
        updateFlags();
    }

    private void updateFlags() {
        flags = NK_WINDOW_BORDER;
        flags |= (!name.isEmpty()) ? NK_WINDOW_TITLE : 0;
        flags |= (closeable) ? NK_WINDOW_CLOSABLE : 0;
    }

    public void init() {
        getTransform().setPosition(new Vector2f(10, 10));
        getTransform().setScale(new Vector2f(300, 200));
    }

    public void render() {
        if (windowRect == null) {
            // need to render it once or it is considered closed
            windowRect = NkRect.create();
            Transform t = getTransform();
            nk_rect(t.getPosition().x, t.getPosition().y, t.getScale().x, t.getScale().y, windowRect);
            nk_begin(getCtx(), name, windowRect, flags);
            nk_end(getCtx());
        } else if (!closeable || !nk_window_is_closed(getCtx(), name)) {
            if (nk_begin(getCtx(), name, windowRect, flags)) {
                for (UIElement e : elements) {
                    if (e == null) {
                        continue;
                    }

                    e.render();
                }
            }
            nk_end(getCtx());
        }
    }

    public void update(float deltaTime) {
        for (UIElement e : elements) {
            if (e == null) {
                continue;
            }

            e.update(deltaTime);
        }
    }

    public void destroy() {

    }
}