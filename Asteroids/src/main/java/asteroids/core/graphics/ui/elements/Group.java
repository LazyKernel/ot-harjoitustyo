package asteroids.core.graphics.ui.elements;

import asteroids.core.graphics.ui.UIElement;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.nuklear.Nuklear.nk_group_begin;

public class Group extends UIElement {
    private String title;
    private int flags;

    public Group() {
        title = "";
        flags = NK_WINDOW_NO_SCROLLBAR;
    }

    public Group(String title, int flags) {
        this.title = title;
        this.flags = flags;
    }

    public void init() {
        if (title.isEmpty() && (flags & NK_WINDOW_TITLE) != 0) {
            flags &= ~NK_WINDOW_TITLE;
        }
    }

    public void render() {
        if (getCtx() == null) {
            return;
        }

        if (nk_group_begin(getCtx(), title, flags)) {
            for (UIElement e : elements) {
                if (e == null) {
                    continue;
                }
                e.render();
            }

            nk_group_end(getCtx());
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
        for (UIElement e : elements) {
            if (e == null) {
                continue;
            }

            e.destroy();
        }
    }
}
