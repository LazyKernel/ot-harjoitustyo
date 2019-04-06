import asteroids.core.Transform;
import org.joml.Vector2f;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransformTest
{
    private Transform t;

    @Before
    public void setUp()
    {
        t = new Transform();
    }

    @Test
    public void testTranslate()
    {
        t.translate(new Vector2f(200, 300));
        assertEquals(new Vector2f(200, 300), t.getPosition());
    }
}
