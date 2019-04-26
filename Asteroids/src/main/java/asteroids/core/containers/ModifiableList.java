package asteroids.core.containers;

import asteroids.core.networking.INetworked;

import java.util.Iterator;

/**
 * Custom List to avoid ConcurrentModificationException.
 *
 * If looping through the array, returned object not guaranteed to be nonnull if the array has been modified in the loop
 *
 * @see java.util.ConcurrentModificationException
 *
 * @param <T> type of objects in the list
 */
public class ModifiableList<T> implements Iterable<T> {
    private Object[] objects;
    private int counter = 0;
    private int numObjects = 0;

    /**
     * Default constructor, creates a list with a capacity of 100
     */
    public ModifiableList() {
        objects = new Object[100];
    }

    /**
     * Constructor
     * @param initialCapacity initial capacity of the list
     */
    public ModifiableList(int initialCapacity) {
        objects = new Object[initialCapacity];
    }

    /**
     * Add an object of type T into the list.
     *
     * Automatically resizes the array if needed.
     *
     * @param e object to add
     */
    public void add(T e) {
        if (counter >= objects.length - 1) {
            resizeArray(objects.length * 2);
        }

        int idx = counter++;
        objects[idx] = e;
        numObjects++;
    }

    private void resizeArray(int newLength) {
        if (newLength == objects.length) {
            return;
        }

        Object[] newObjects = new Object[newLength];

        int newCounter = 0;
        counter = 0;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                continue;
            }

            if (newCounter == newLength) {
                System.out.println("Objects potentially lost.");
                break;
            }

            int newIdx = newCounter++;
            newObjects[newIdx] = objects[i];
            counter++;
        }

        objects = newObjects;
        numObjects = counter;
    }

    /**
     * Remove object from the list
     * @param t reference to object
     */
    public void remove(T t) {
        if (numObjects <= 0) {
            return;
        }

        if (t instanceof Entity) {
            Entity e = (Entity) t;
            if (e.getEntityId() >= 0) {
                remove(e.getEntityId());
                return;
            }
        }

        for (int i = 0; i < counter; i++) {
            if (objects[i] == null) {
                continue;
            }

            if (objects[i].equals(t)) {
                objects[i] = null;
                numObjects--;
                break;
            }
        }
    }

    /**
     * Removes an entity from the list with specified entity id.
     *
     * @see Entity
     *
     * @param entityId entity id of the entity to remove
     */
    public void remove(int entityId) {
        if (numObjects <= 0) {
            return;
        }

        if (entityId < 0) {
            return;
        }

        for (int i = 0; i < counter; i++) {
            Object o = objects[i];
            if (o instanceof Entity) {
                Entity e = (Entity) o;
                if (e.getEntityId() == entityId) {
                    objects[i] = null;
                    numObjects--;
                    break;
                }
            }
        }
    }

    /**
     * Returns an object in a specific position in the list
     *
     * Returns null if index is out of bounds
     *
     * @param idx index
     * @return object of type t at idx, not guaranteed to be nonnull
     */
    @SuppressWarnings("unchecked")
    public T get(int idx) {
        if (idx < 0 || idx >= counter) {
            return null;
        }

        return (T) objects[idx];
    }

    /**
     * Returns an entity with the specified entity id
     *
     * Returns null if entity id is less than 0
     *
     * @param entityId entity id of the entity to find
     * @return Entity with the specified entity id
     */
    @SuppressWarnings("unchecked")
    public T getWithEntityId(int entityId) {
        if (entityId < 0) {
            return null;
        }

        for (int i = 0; i < counter; i++) {
            Object o = objects[i];
            if (o instanceof Entity) {
                Entity e = (Entity) o;
                if (e.getEntityId() == entityId) {
                    return (T) objects[i];
                }
            }
        }

        return null;
    }

    /**
     * Returns an INetworked with the specified net id
     *
     * Returns null if net id is less than 0
     *
     * @param netId net id of the networked component to find
     * @return INetworked with the specified net id
     */
    @SuppressWarnings("unchecked")
    public T getWithNetId(int netId) {
        if (netId < 0) {
            return null;
        }

        for (int i = 0; i < counter; i++) {
            Object o = objects[i];
            if (o instanceof INetworked) {
                INetworked e = (INetworked) o;
                if (e.getNetId() == netId) {
                    return (T) objects[i];
                }
            }
        }

        return null;
    }

    public int size() {
        return numObjects;
    }

    public int capacity() {
        return objects.length;
    }

    /**
     * Clears the list
     *
     * Doesn't destroy contained objects
     */
    public void clear() {
        for (int i = 0; i < counter; i++) {
            objects[i] = null;
        }
        numObjects = 0;
    }

    /**
     * Iterator to the list
     *
     * @see ModifiableListIterator
     *
     * @return iterator to the list
     */
    @Override
    public Iterator<T> iterator() {
        return new ModifiableListIterator<>(objects, counter);
    }
}
