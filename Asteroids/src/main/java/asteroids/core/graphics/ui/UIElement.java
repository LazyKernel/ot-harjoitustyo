package asteroids.core.graphics.ui;

import asteroids.core.containers.ModifiableList;
import asteroids.core.containers.Transform;
import org.lwjgl.nuklear.NkContext;

public abstract class UIElement {
    protected ModifiableList<UIElement> elements = new ModifiableList<>(10);

    private Transform transform = new Transform();
    private NkContext ctx;

    public abstract void init();
    public abstract void render();
    public abstract void update(float deltaTime);
    public abstract void destroy();

    public void addElement(UIElement element) {
        element.setCtx(ctx);
        elements.add(element);
        element.init();
    }

    public void removeElement(UIElement element) {
        element.destroy();
        elements.remove(element);
    }

    public Transform getTransform() {
        return transform;
    }

    public NkContext getCtx() {
        return ctx;
    }

    public void setCtx(NkContext ctx) {
        this.ctx = ctx;
    }
}
