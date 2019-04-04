package asteroids.game;

import java.util.ArrayList;
import java.util.List;

public class Entity
{
    private List<EntityComponent> entityComponents = new ArrayList<>();
    private Transform transform = new Transform();

    public void render()
    {
        for (EntityComponent c : entityComponents)
        {
            c.render();
        }
    }

    public void update(float deltaTime)
    {
        for (EntityComponent c : entityComponents)
        {
            c.update(deltaTime);
        }
    }

    public void destroy()
    {
        for (EntityComponent c : entityComponents)
        {
            c.destroy();
        }

        entityComponents.clear();
    }

    public void addComponent(EntityComponent component)
    {
        entityComponents.add(component);
        component.setParent(this);
        component.init();
    }

    public void removeComponent(EntityComponent component)
    {
        component.destroy();
        entityComponents.remove(component);
    }

    public Transform getTransform() { return transform; }
    public void setTransform(Transform t) { this.transform = t; }

}
