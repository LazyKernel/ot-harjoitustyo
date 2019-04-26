package asteroids.core.graphics.ui.elements;

import asteroids.core.graphics.ui.UIElement;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class DynamicRow extends UIElement {

    private float height;
    private int itemsPerRow;

    public DynamicRow() {
        height = 30;
        itemsPerRow = 1;
    }

    public DynamicRow(float height, int itemsPerRow) {
        this.height = height;
        this.itemsPerRow = itemsPerRow;
    }

    public void init() {
    }

    public void render() {
        if (getCtx() == null) {
            return;
        }

        nk_layout_row_dynamic(getCtx(), height, itemsPerRow);

        for (UIElement e : elements) {
            if (e == null) {
                continue;
            }
            e.render();
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
