import asteroids.core.containers.Entity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntityTest {

    TestCounter counter;
    Entity entity;

    @Before
    public void setUp() {
        entity = new Entity();
        counter = new TestCounter(4);
    }

    @Test
    public void testInit() {
        for (int i = 0; i < 100; i++) {
            entity.addComponent(new TestEntityComponent(counter));
        }

        assertEquals(100, counter.get(0));
    }

    @Test
    public void testRender() {
        for (int i = 0; i < 100; i++) {
            entity.addComponent(new TestEntityComponent(counter));
        }

        entity.render();
        assertEquals(100, counter.get(1));
    }

    @Test
    public void testUpdate() {
        for (int i = 0; i < 100; i++) {
            entity.addComponent(new TestEntityComponent(counter));
        }

        entity.update(0.1f);
        assertEquals(100, counter.get(2));
    }

    @Test
    public void testDestroy() {
        for (int i = 0; i < 100; i++) {
            entity.addComponent(new TestEntityComponent(counter));
        }

        entity.destroy();
        assertEquals(100, counter.get(3));
    }

    @Test
    public void testRemove() {
        TestEntityComponent t = new TestEntityComponent(counter);
        entity.addComponent(t);
        entity.removeComponent(t);
        assertEquals(1, counter.get(3));
    }
}
