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
        list.add(new Entity());
        list.add(x);
        assertEquals(2, list.size());
        list.remove(0);
        assertEquals(1, list.size());
        list.remove(x);
        assertEquals(0, list.size());
    }

    @Test
    public void testSize()
    {
        for (int i = 0; i < 50; i++)
        {
            list.add(new Entity());
        }

        assertEquals(50, list.size());

        for (int i = 0; i < 25; i++)
        {
            list.remove(i * 2);
        }

        assertEquals(25, list.size());
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
