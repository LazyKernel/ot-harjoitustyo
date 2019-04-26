package asteroids.core.graphics.ui.elements;

import asteroids.core.containers.Entity;
import asteroids.core.containers.ModifiableList;
import asteroids.core.graphics.ui.UIElement;

import static org.lwjgl.nuklear.Nuklear.*;

public class StaticRow extends UIElement {
    class Entry {
        public UIElement element;
        public float width;

        public Entry(UIElement element, float width) {
            this.element = element;
            this.width = width;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (!(o instanceof Entry)) {
                return false;
            }

            Entry other = (Entry) o;
            return element.equals(other.element);
        }
    }

    private ModifiableList<Entry> entries = new ModifiableList<>(10);
    private float height;
    private int itemsPerRow;

    public StaticRow() {
        height = 30;
        itemsPerRow = 1;
    }

    public StaticRow(float height, int itemsPerRow) {
        this.height = height;
        this.itemsPerRow = itemsPerRow;
    }

    public void init() {

    }

    public void render() {
        if (getCtx() == null) {
            return;
        }

        nk_layout_row_begin(getCtx(), NK_STATIC, height, itemsPerRow);

        for (Entry e : entries) {
            if (e == null) {
                continue;
            }
            nk_layout_row_push(getCtx(), e.width);
            e.element.render();
        }

        nk_layout_row_end(getCtx());
    }

    public void update(float deltaTime) {
        for (Entry e : entries) {
            if (e == null) {
                continue;
            }
            e.element.update(deltaTime);
        }
    }

    public void destroy() {
        for (Entry e : entries) {
            if (e == null) {
                continue;
            }

            e.element.destroy();
        }
    }

    @Override
    public void addElement(UIElement element) {
        element.setCtx(getCtx());
        entries.add(new Entry(element, 0.0f));
        element.init();
    }

    @Override
    public void removeElement(UIElement element) {
        element.destroy();
        entries.remove(new Entry(element, 0.0f));
    }

    public void addElement(UIElement element, float width) {
        element.setCtx(getCtx());
        entries.add(new Entry(element, width));
        element.init();
    }
}
