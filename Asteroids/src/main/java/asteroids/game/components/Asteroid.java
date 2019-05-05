package asteroids.game.components;

import asteroids.core.containers.Transform;
import asteroids.core.graphics.Mesh;
import asteroids.core.networking.INetworked;
import asteroids.core.physics.ICollider;
import asteroids.game.AsteroidsGame;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;

/**
 * Asteroid component
 */
public class Asteroid extends INetworked implements ICollider {

    private static final Vector3f[] POINTS = new Vector3f[]{
        new Vector3f(0, -1, 0.0f), new Vector3f(0.5f, -0.7f, 0.0f), new Vector3f(1, 0, 0.0f),
        new Vector3f(0.6f, 0.5f, 0.0f), new Vector3f(0.3f, 0.8f, 0.0f), new Vector3f(-0.3f, 0.8f, 0.0f),
        new Vector3f(-1, 0.6f, 0.0f), new Vector3f(-0.6f, -0.2f, 0.0f), new Vector3f(-0.7f, -0.6f, 0.0f),
        new Vector3f(0, -1, 0.0f)
    };

    private static final Vector3f[] COLLISION_POINTS = new Vector3f[] {
        new Vector3f(0, -1, 0.0f), new Vector3f(0.5f, -0.7f, 0.0f), new Vector3f(1, 0, 0.0f),
        new Vector3f(0.6f, 0.5f, 0.0f), new Vector3f(0.3f, 0.8f, 0.0f), new Vector3f(-0.3f, 0.8f, 0.0f),
        new Vector3f(-1, 0.6f, 0.0f), new Vector3f(-0.7f, -0.6f, 0.0f)
    };

    Random rand = new Random();

    private Mesh asteroidMesh;
    private float largestRadius = 0.0f;

    private static int numAsteroids = 0;

    /**
     * Creates the mesh if this is not a headless server also sets a random size if this is a server.
     */
    @Override
    public void init() {
        if (!getEntity().getRenderer().getIsHeadlessServer() || getEntity().getRenderer().getIsServerVisualDebug()) {
            asteroidMesh = new Mesh();
            asteroidMesh.setPoints(POINTS, GL_LINE_STRIP);
            getEntity().addComponent(asteroidMesh);
        }

        if (getEntity().getRenderer().getIsServer()) {
            int size = rand.nextInt(75) + 25;
            getTransform().setScale(new Vector2f(size, size));
        }

        numAsteroids++;
    }

    @Override
    public void render() {

    }

    /**
     * Moves the asteroid forward and if this is a server makes sure there's at least 5 asteroids present at all times.
     * Also if this a server queues asteroids for removal if they go too far from the playable area.
     * @param deltaTime seconds since last update
     */
    @Override
    public void update(float deltaTime) {
        Vector3f forward = new Vector3f(0.0f, 1.0f, 0.0f);
        forward.rotateZ(getTransform().getRotation());
        getTransform().translate(forward.mul(deltaTime * 50));

        if (getEntity().getRenderer().getIsServer()) {
            Vector2f pos = getTransform().getPosition();
            if (pos.x > 1000.0f || pos.x < -1000.0f || pos.y > 800.0f || pos.y < -800.0f) {
                getEntity().getRenderer().getNetworking().queueForRemoval(this);
            }

            // have at least 5 asteroids at all times
            if (numAsteroids < 5) {
                AsteroidsGame game = (AsteroidsGame) getEntity().getRenderer().getGame();
                game.createAsteroid();
            }
        }
    }

    @Override
    public void destroy() {
        numAsteroids--;
    }

    /**
     * If this is a server sends the current transform to all clients
     * @param objects list to add serialized objects to
     * @param isServer is this a server
     */
    @Override
    public void netSerialize(List<Object> objects, boolean isServer) {
        if (isServer) {
            objects.add(getTransform());
        }
    }

    /**
     * If this is a client, sets the transform received from a server
     * @param objects objects to deserialize
     * @param deltaTime time since last update
     * @param isServer is this a server
     */
    @Override
    public void netDeserialize(List<Object> objects, float deltaTime, boolean isServer) {
        if (!isServer) {
            for (Object o : objects) {
                if (o instanceof Transform) {
                    setTransform((Transform) o);
                }
            }
        }
    }

    @Override
    public Vector3f[] getPoints() {
        return COLLISION_POINTS;
    }

    /**
     * If this is a server and this was hit by a bullet, queues this for removal and adds 10 score to the bullet owner.
     * @param other collided with other collider
     * @param isServer is this a server
     */
    @Override
    public void hit(ICollider other, boolean isServer) {
        if (isServer) {
            if (other instanceof Bullet) {
                Bullet b = (Bullet) other;
                b.getOwnerPlayer().addScore(10);
                getEntity().getRenderer().getNetworking().queueForRemoval(this);
            }
        }
    }

    /**
     * Calculates the value or returns a precalculated value
     * @see ICollider#calculateLargestRadius()
     * @return largest radius calculated by calculateLargestRadius()
     */
    @Override
    public float getLargestRadius() {
        if (largestRadius == 0.0f) {
            largestRadius = calculateLargestRadius();
        }

        return largestRadius;
    }
}
