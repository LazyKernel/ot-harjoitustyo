package asteroids;

import asteroids.core.physics.PhysicsEngine;
import asteroids.helpers.TestCollider;
import asteroids.helpers.TestCounter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PhysicsTest {
    private static final Vector3f[] PLAYER_POINTS = new Vector3f[]{ new Vector3f(-0.75f, -1.0f, 0.0f), new Vector3f(0.75f, -1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(-0.75f, -1.0f, 0.0f) };
    private static final Vector3f[] BULLET_POINTS = new Vector3f[] {
            new Vector3f(-0.1f, 1.0f, 0.0f), new Vector3f(0.1f, 1.0f, 0.0f),
            new Vector3f(0.1f, -1.0f, 0.0f),new Vector3f(-0.1f, -1.0f, 0.0f)
    };

    private PhysicsEngine engine;
    private TestCounter bulletCounter;
    private TestCollider bulletCollider;
    private TestCounter playerCounter;
    private TestCollider playerCollider;

    @Before
    public void setUp() {
        engine = new PhysicsEngine();
        bulletCounter = new TestCounter(1);
        bulletCollider = new TestCollider(BULLET_POINTS, bulletCounter);
        playerCounter = new TestCounter(1);
        playerCollider = new TestCollider(PLAYER_POINTS, playerCounter);
        engine.addCollider(bulletCollider);
        engine.addCollider(playerCollider);
    }

    @Test
    public void testCollision() {
        bulletCollider.getTransform().setPosition(new Vector2f(-200, -80));
        bulletCollider.getTransform().setRotation((float) -Math.PI / 8);
        playerCollider.getTransform().setPosition(new Vector2f(-200, -100));
        playerCollider.getTransform().setRotation((float) Math.PI / 7);
        engine.update(1, true);
        assertEquals(1, playerCounter.get(0));
        assertEquals(1, bulletCounter.get(0));
    }
}
