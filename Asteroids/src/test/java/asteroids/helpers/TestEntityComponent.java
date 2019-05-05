package asteroids.helpers;

import asteroids.core.containers.EntityComponent;
import asteroids.helpers.TestCounter;

public class TestEntityComponent extends EntityComponent
{
    public int uniqueId;
    public TestCounter counter;

    public TestEntityComponent() {
        uniqueId = 0;
        counter = null;
    }

    public TestEntityComponent(TestCounter counter) {
        uniqueId = 0;
        this.counter = counter;
    }

    public void init()
    {
        if (counter != null) {
            counter.increment(0);
        }
    }

    public void render()
    {
        if (counter != null) {
            counter.increment(1);
        }
    }

    public void update(float deltaTime)
    {
        if (counter != null) {
            counter.increment(2);
        }
    }

    public void destroy()
    {
        if (counter != null) {
            counter.increment(3);
        }
    }
}
