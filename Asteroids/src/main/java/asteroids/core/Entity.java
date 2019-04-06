package asteroids.core;

import asteroids.core.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private ModifiableList<EntityComponent> entityComponents = new ModifiableList<>(5);
    private Transform transform = new Transform();

    private Renderer renderer;

    private int entityId = -1;

    public void render() {
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            c.render();
        }
    }

    public void update(float deltaTime) {
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            c.update(deltaTime);
        }
    }

    public void destroy() {
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            c.destroy();
        }

        entityComponents.clear();
    }

    public void addComponent(EntityComponent component) {
        entityComponents.add(component);
        component.setEntity(this);
        component.init();
    }

    public <T> List<T> getComponentsOfType(Class<T> type) {
        List<T> comps = new ArrayList<>();
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            if (c.getClass() == type) {
                comps.add(type.cast(c));
            }
        }
        return comps;
    }

    public void removeComponent(EntityComponent component) {
        component.destroy();
        entityComponents.remove(component);
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform t) {
        this.transform = t;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void setEntityId(int id) {
        this.entityId = id;
    }

    public int getEntityId() {
        return entityId;
    }
}
