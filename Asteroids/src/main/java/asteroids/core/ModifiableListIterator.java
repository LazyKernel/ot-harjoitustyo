package asteroids.core;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;

public class ModifiableListIterator<T> implements Iterator<T> {
    private Object[] objects;
    private int curPos;
    private int entityCounter;

    public ModifiableListIterator(Object[] objects, int entityCounter) {
        this.objects = objects;
        this.entityCounter = entityCounter;
        this.curPos = 0;
    }

    @Override
    public boolean hasNext() {
        return curPos < entityCounter;
    }

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

    @Override
    public void remove() {
        throw new NotImplementedException();
    }
}
