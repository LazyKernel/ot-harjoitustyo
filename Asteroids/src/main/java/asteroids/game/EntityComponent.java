package asteroids.game;

public abstract class EntityComponent
{
    protected Entity parent;

    public abstract void init();

    public abstract void render();

    public abstract void update(float deltaTime);

    public abstract void destroy();

    public Entity getParent()
    {
        return parent;
    }

    public void setParent(Entity parent)
    {
        this.parent = parent;
    }

    public Transform getTransform()
    {
        return parent.getTransform();
    }
}
