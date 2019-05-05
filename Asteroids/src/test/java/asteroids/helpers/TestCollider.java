package asteroids.helpers;

import asteroids.core.containers.Transform;
import asteroids.core.physics.ICollider;
import org.joml.Vector3f;

public class TestCollider implements ICollider {
    private Vector3f[] points;
    private TestCounter counter;
    private Transform transform = new Transform();

    private float largestRadius = 0.0f;

    public TestCollider(Vector3f[] points, TestCounter counter) {
        this.points = points;
        this.counter = counter;
    }

    @Override
    public Vector3f[] getPoints() {
        return points;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void hit(ICollider other, boolean isServer) {
        counter.increment(0);
    }

    @Override
    public float getLargestRadius() {
        if (largestRadius == 0.0f) {
            largestRadius = calculateLargestRadius();
        }

        return largestRadius;
    }
}
