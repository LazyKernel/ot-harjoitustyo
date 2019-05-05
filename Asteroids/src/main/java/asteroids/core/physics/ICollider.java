package asteroids.core.physics;

import asteroids.core.containers.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Interface for a collider
 */
public interface ICollider {

    /**
     * Get points defining the collider
     * @return array of 3d vectors
     */
    Vector3f[] getPoints();

    /**
     * Get the transform for the collider
     * @return transform
     */
    Transform getTransform();

    /**
     * Called when this collider collides with another collider
     * @param other collided with other collider
     * @param isServer is this a server
     */
    void hit(ICollider other, boolean isServer);

    /**
     * Helper function for physics calculations. Determines the point farthest in a specified direction.
     * @param dir direction
     * @return point in world coordinates
     */
    default Vector2f getFarthestPoint(Vector3f dir) {
        Vector3f farthestPoint = new Vector3f();
        float length = 0.0f;
        for (int i = 0; i < getPoints().length; i++) {
            Vector4f transformedPoint = new Vector4f(getPoints()[i], 1.0f).mul(getTransform().getScaledTransformMatrix());
            Vector3f point = new Vector3f(transformedPoint.x, transformedPoint.y, transformedPoint.z);

            float len = point.dot(dir);

            if (len > length) {
                length = len;
                farthestPoint = point;
            }
        }

        return new Vector2f(farthestPoint.x, farthestPoint.y);
    }

    /**
     * Calculates the rough bouding box for the collider
     *
     * [ top left, top right, bottom left, bottom right]
     * @return array of vertices
     */
    default Vector2f[] getBoundingBox() {
        Vector2f p = getTransform().getPosition();
        Vector2f s = getTransform().getScale();
        return new Vector2f[] {
            new Vector2f(p.x - getLargestRadius() * s.x, p.y + getLargestRadius() * s.y),
            new Vector2f(p.x + getLargestRadius() * s.x, p.y + getLargestRadius() * s.y),
            new Vector2f(p.x - getLargestRadius() * s.x, p.y - getLargestRadius() * s.y),
            new Vector2f(p.x + getLargestRadius() * s.x, p.y - getLargestRadius() * s.y)
        };
    }

    /**
     * Helper function for calculating the furthest point in the collider from origin
     * @return smallest radius for containing all the points in collider in a circle
     */
    default float calculateLargestRadius() {
        float largestRadius = 0.0f;
        for (int i = 0; i < getPoints().length; i++) {
            largestRadius = Math.max(Math.abs(getPoints()[i].x), largestRadius);
            largestRadius = Math.max(Math.abs(getPoints()[i].y), largestRadius);
        }
        return largestRadius;
    }

    float getLargestRadius();
}
