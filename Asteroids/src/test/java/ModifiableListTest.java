import asteroids.core.containers.Entity;
import asteroids.core.containers.ModifiableList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ModifiableListTest
{
    ModifiableList<Entity> list;

    @Before
    public void setUp()
    {
        list = new ModifiableList<>();
    }

    @Test
    public void testAdding()
    {
        list.add(new Entity());
        assertEquals(1, list.size());
    }

    @Test
    public void testRemoving()
    {
        Entity x = new Entity();
        Entity y = new Entity();
        Entity z = new Entity();
        y.setEntityId(5);
        z.setEntityId(15);
        list.add(y);
        list.add(x);
        list.add(z);
        assertEquals(3, list.size());
        list.remove(5);
        assertEquals(2, list.size());
        list.remove(x);
        assertEquals(1, list.size());
        list.remove(z);
        assertEquals(0, list.size());
    }

    @Test
    public void testGetters() {
        Entity x = new Entity();
        list.add(x);
        assertEquals(x, list.get(0));
        x.setEntityId(80);
        assertEquals(x, list.getWithEntityId(80));
    }

    @Test
    public void testSize()
    {
        for (int i = 0; i < 50; i++)
        {
            Entity e = new Entity();
            e.setEntityId(i);
            list.add(e);
        }

        assertEquals(50, list.size());

        for (int i = 0; i < 25; i++)
        {
            list.remove(i * 2);
        }

        assertEquals(25, list.size());
    }

    @Test
    public void testCapacity() {
        ModifiableList<Object> l = new ModifiableList<>(50);
        assertEquals(50, l.capacity());
    }

    @Test
    public void testResize() {
        for (int i = 0; i < 100; i++) {
            list.add(new Entity());
        }

        assertEquals(100, list.size());
        assertEquals(200, list.capacity());
    }

    @Test
    public void testIterator()
    {
        for (int i = 0; i < 50; i++)
        {
            Entity e = new Entity();
            TestEntityComponent test = new TestEntityComponent();
            test.uniqueId = i;
            e.addComponent(test);
            list.add(e);
        }

        int counter = 0;
        for (Entity e : list)
        {
            // should only have one
            List<TestEntityComponent> components = e.getComponentsOfType(TestEntityComponent.class);
            assertEquals(counter, components.get(0).uniqueId);
            counter++;
        }
    }
}
