package asteroids.core;

public abstract class EntityComponent {
    protected Entity parent;

    public abstract void init();

    public abstract void render();

    public abstract void update(float deltaTime);

    public abstract void destroy();

    public Entity getEntity() {
        return parent;
    }

    public void setEntity(Entity parent) {
        this.parent = parent;
    }

    public Transform getTransform() {
        return parent.getTransform();
    }
}
