package asteroids.core.containers;

import asteroids.core.graphics.Renderer;
import asteroids.core.networking.INetworked;
import asteroids.core.networking.INetworking;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private ModifiableList<EntityComponent> entityComponents = new ModifiableList<>(5);
    private Transform transform = new Transform();

    private int entityId = -1;

    /**
     * Calls the render method of all components in this entity
     *
     * @see EntityComponent#render()
     */
    public void render() {
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            c.render();
        }
    }

    /**
     * Calls the update method of all components in this entity
     *
     * @see EntityComponent#update(float)
     *
     * @param deltaTime time since last frame in seconds
     */
    public void update(float deltaTime) {
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            c.update(deltaTime);
        }
    }

    /**
     * Destroys all components in this entity and clears the list
     *
     * If the component is an instance of INetworked it will remove that from networking
     * @see INetworking#removeNetworkedComponent(INetworked)
     */
    public void destroy() {
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            removeNetworkedComponentIfPossible(c);
            c.destroy();
        }

        entityComponents.clear();
    }

    /**
     * Adds a component to this entity.
     *
     * Sets component parent to this and calls component's init method.
     *
     * Adds component to networking if it's an instance of INetworked
     *
     * @see EntityComponent#init()
     * @see INetworking#addNetworkedComponent(INetworked)
     *
     * @param component component to add to this entity
     */
    public void addComponent(EntityComponent component) {
        entityComponents.add(component);
        component.setEntity(this);
        component.init();

        // doing this to fix testing
        Renderer renderer = getRenderer();

        if (renderer != null) {
            INetworking networking = renderer.getNetworking();
            if (INetworked.class.isAssignableFrom(component.getClass())) {
                networking.addNetworkedComponent((INetworked) component);
            }
        }
    }

    /**
     * Returns a list of components of specified type
     *
     * @param type class of desired component
     * @param <T> automatically assigned from type
     * @return list of components of type
     */
    public <T> List<T> getComponentsOfType(Class<T> type) {
        List<T> comps = new ArrayList<>();
        for (EntityComponent c : entityComponents) {
            if (c == null) {
                continue;
            }

            if (type.isAssignableFrom(c.getClass())) {
                comps.add(type.cast(c));
            }
        }
        return comps;
    }

    /**
     * Removes the component from entity if this entity is it's parent.
     * Also removes it from networking if it's an instance of INetworked
     *
     * @see INetworking#removeNetworkedComponent(INetworked)
     *
     * @param component reference to the component (expects non null)
     */
    public void removeComponent(EntityComponent component) {
        removeNetworkedComponentIfPossible(component);
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
        return Renderer.getRenderer();
    }

    public void setEntityId(int id) {
        this.entityId = id;
    }

    public int getEntityId() {
        return entityId;
    }

    private void removeNetworkedComponentIfPossible(EntityComponent component) {
        Renderer renderer = getRenderer();

        if (renderer != null) {
            INetworking networking = renderer.getNetworking();
            if (INetworked.class.isAssignableFrom(component.getClass())) {
                networking.removeNetworkedComponent((INetworked) component);
            }
        }
    }
}
