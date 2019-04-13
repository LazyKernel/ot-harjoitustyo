package asteroids.core.containers;

import asteroids.core.graphics.Renderer;

import java.util.Iterator;

public class ModifiableList<T> implements Iterable<T> {
    private Object[] objects;
    private int counter = 0;
    private int numObjects = 0;

    public ModifiableList() {
        objects = new Object[100];
    }

    public ModifiableList(int initialCapacity) {
        objects = new Object[initialCapacity];
    }

    public void add(T e) {
        if (counter >= objects.length - 1) {
            resizeArray(objects.length * 2);
        }

        /*if (numObjects <= objects.length / 4) {
            resizeArray(objects.length / 4);
        }*/

        int idx = counter++;
        objects[idx] = e;

        if (e.getClass() == Entity.class && Renderer.getRenderer().getIsServer()) {
            ((Entity) e).setEntityId(idx);
        }
        numObjects++;
    }

    private void resizeArray(int newLength) {
        if (newLength == objects.length) {
            return;
        }

        if (newLength < 100) {
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
                System.out.println("Entities potentially lost.");
                break;
            }

            int newIdx = newCounter++;
            newObjects[newIdx] = objects[i];
            counter++;
        }

        objects = newObjects;
        numObjects = counter;
    }

    public void remove(T t) {
        if (t.getClass() == Entity.class) {
            remove(((Entity) t).getEntityId());
            return;
        }

        for (int i = 0; i < counter; i++) {
            if (objects[i] == null) {
                continue;
            }

            if (objects[i].equals(t)) {
                objects[i] = null;
            }
        }
    }

    public void remove(int entityId) {
        if (entityId < 0) {
            return;
        }

        for (int i = 0; i < counter; i++) {
            Object o = objects[i];
            if (o instanceof Entity) {
                Entity e = (Entity) o;
                if (e.getEntityId() == entityId) {
                    objects[i] = null;
                    break;
                }
            }
        }

        numObjects--;
    }

    @SuppressWarnings("unchecked")
    public T get(int idx) {
        if (idx < 0 || idx >= counter) {
            return null;
        }

        return (T) objects[idx];
    }

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

    public int size() {
        return numObjects;
    }

    public void clear() {
        for (int i = 0; i < counter; i++) {
            objects[i] = null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ModifiableListIterator<>(objects, counter);
    }
}
