package asteroids.core.containers;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;

public class ModifiableListIterator<T> implements Iterator<T> {
    private Object[] objects;
    private int curPos;
    private int entityCounter;

    ModifiableListIterator(Object[] objects, int entityCounter) {
        this.objects = objects;
        this.entityCounter = entityCounter;
        this.curPos = 0;
    }

    /**
     * Checks if the list has more elements
     *
     * Might return true even if no more elements exists if they have been deleted within the loop
     *
     * @return true if more objects are on the list, false otherwise unless objects have been deleted
     */
    @Override
    public boolean hasNext() {
        return curPos < entityCounter;
    }

    /**
     * Gets the next element in the list
     *
     * Not guaranteed to be nonnull if the list has been modified within the loop
     *
     * @return next element in the list or null
     */
    @SuppressWarnings("unchecked")
    @Override
    public T next() {
        T e = null;
        while (curPos < entityCounter) {
            curPos++;
            if (objects[curPos - 1] != null) {
                e = ((T) objects[curPos - 1]);
                break;
            }
        }
        return e;
    }

    /**
     * Not implemented yet
     */
    @Override
    public void remove() {
        throw new NotImplementedException();
    }
}
