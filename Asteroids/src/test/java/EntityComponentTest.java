import asteroids.core.Entity;
import asteroids.core.EntityComponent;
import asteroids.core.Transform;
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
        Transform t = new Transform();
        entityComponent.setTransform(t);
        assertEquals(t, entityComponent.getTransform());
    }
}
