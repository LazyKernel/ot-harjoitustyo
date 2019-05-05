package asteroids.core.containers;

/**
 * Base class for all components
 */
public abstract class EntityComponent {
    protected Entity parent;

    /**
     * Ran once after component is added to an entity
     * @see Entity
     */
    public abstract void init();

    /**
     * Ran every frame after update unless on a headless server
     */
    public abstract void render();

    /**
     * Ran every frame
     * @param deltaTime seconds since last update
     */
    public abstract void update(float deltaTime);

    /**
     * Ran after parent entity destroyed or this component removed
     * @see Entity#removeComponent(EntityComponent)
     */
    public abstract void destroy();

    public Entity getEntity() {
        return parent;
    }

    public void setEntity(Entity parent) {
        this.parent = parent;
    }

    /**
     * Returns parent entity's transform
     * @return transform of parent
     */
    public Transform getTransform() {
        return parent.getTransform();
    }

    /**
     * Sets parent entity's transform
     * @param t new transform
     */
    public void setTransform(Transform t) {
        parent.setTransform(t);
    }
}
