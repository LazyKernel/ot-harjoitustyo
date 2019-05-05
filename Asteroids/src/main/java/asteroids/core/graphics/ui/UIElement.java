package asteroids.core.graphics.ui;

import asteroids.core.containers.ModifiableList;
import asteroids.core.containers.Transform;
import org.lwjgl.nuklear.NkContext;

/**
 * Base class for all other ui elements
 */
public abstract class UIElement {
    protected ModifiableList<UIElement> elements = new ModifiableList<>(10);

    private Transform transform = new Transform();
    private NkContext ctx;

    /**
     * Ran when added to UIManager or to another UIElement that supports children
     * @see UIManager
     */
    public abstract void init();

    /**
     * Ran every frame before update unless this is a server
     */
    public abstract void render();

    /**
     * Ran every frame unless this is a server
     * @param deltaTime seconds since last update
     */
    public abstract void update(float deltaTime);

    /**
     * Ran after removed from UIManager or parent destroyed
     */
    public abstract void destroy();

    /**
     * Add element and initialize it
     * @see UIElement#init()
     * @param element element to add
     */
    public void addElement(UIElement element) {
        element.setCtx(ctx);
        elements.add(element);
        element.init();
    }

    /**
     * Remove added element and destroy it
     * @see UIElement#destroy()
     * @param element element to remove
     */
    public void removeElement(UIElement element) {
        element.destroy();
        elements.remove(element);
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public NkContext getCtx() {
        return ctx;
    }

    public void setCtx(NkContext ctx) {
        this.ctx = ctx;
    }
}
