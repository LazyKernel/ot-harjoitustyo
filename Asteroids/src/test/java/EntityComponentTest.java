import asteroids.core.containers.Entity;
import asteroids.core.containers.EntityComponent;
import asteroids.core.containers.Transform;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityComponentTest
{
    private EntityComponent entityComponent;

    @Before
    public void startUp() {
        entityComponent = new EntityComponent() {
            @Override
            public void init() {

            }

            @Override
            public void render() {

            }

            @Override
            public void update(float deltaTime) {

            }

            @Override
            public void destroy() {

            }
        };
    }

    @Test
    public void testEntityGetterSetter() {
        Entity entity = new Entity();
        entityComponent.setEntity(entity);
        assertEquals(entity, entityComponent.getEntity());
    }

    @Test
    public void testTransformGetterSetter() {
        Entity entity = new Entity();
        entityComponent.setEntity(entity);
        Transform t = new Transform();
        entityComponent.setTransform(t);
        assertEquals(t, entityComponent.getTransform());
    }
}
