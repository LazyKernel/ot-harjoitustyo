package asteroids.game.components;

import asteroids.core.containers.Transform;
import asteroids.core.graphics.Mesh;
import asteroids.core.networking.INetworked;
import asteroids.core.physics.ICollider;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * Bullet component
 */
public class Bullet extends INetworked implements ICollider {
    private static final Vector3f[] POINTS = new Vector3f[]{ new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f) };
    private Mesh bulletMesh;

    private float largestRadius = 0.0f;

    private Player ownerPlayer;

    public Bullet() {
        ownerPlayer = null;
    }

    public Bullet(Player owner) {
        this.ownerPlayer = owner;
    }

    /**
     * Creates the mesh if this is not a headless server
     */
    @Override
    public void init() {
        if (!getEntity().getRenderer().getIsHeadlessServer() || getEntity().getRenderer().getIsServerVisualDebug()) {
            bulletMesh = new Mesh();
            bulletMesh.setPoints(POINTS, GL_LINES);
            getEntity().addComponent(bulletMesh);
        }

        getTransform().setScale(new Vector2f(10, 10));
    }

    @Override
    public void render() {

    }

    /**
     * Moves the bullet forward. If this is a server, removes the bullet if it goes too far from the play area.
     * @param deltaTime seconds since last update
     */
    @Override
    public void update(float deltaTime) {
        Vector3f forward = new Vector3f(0.0f, 1.0f, 0.0f);
        forward.rotateZ(getTransform().getRotation());
        getTransform().translate(forward.mul(deltaTime * 1200));

        if (getEntity().getRenderer().getIsServer()) {
            Vector2f pos = getTransform().getPosition();
            if (pos.x > 820.0f || pos.x < -820.0f || pos.y > 620.0f || pos.y < -620.0f) {
                getEntity().getRenderer().getNetworking().queueForRemoval(this);
            }
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * If this is a server sends the transform to all clients
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
     * If this is a client sets the transform received from the server
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

    /**
     * Returns a small rectangle enclosing the bullet
     * @return array of vectors
     */
    @Override
    public Vector3f[] getPoints() {
        return new Vector3f[] {
            new Vector3f(-0.1f, 1.0f, 0.0f), new Vector3f(0.1f, 1.0f, 0.0f),
            new Vector3f(0.1f, -1.0f, 0.0f), new Vector3f(-0.1f, -1.0f, 0.0f)
        };
    }

    /**
     * If this was hit by another component, queues this for removal if we are the server
     * @param other collided with other collider
     * @param isServer is this a server
     */
    @Override
    public void hit(ICollider other, boolean isServer) {
        if (isServer) {
            if (other instanceof Player) {
                Player p = (Player) other;
                if (!p.getOwner().equals(getOwner())) {
                    getEntity().getRenderer().getNetworking().queueForRemoval(this);
                }
            } else if (other instanceof Bullet) {
                Bullet b = (Bullet) other;
                if (!b.getOwner().equals(getOwner())) {
                    getEntity().getRenderer().getNetworking().queueForRemoval(this);
                }
            } else if (other instanceof Asteroid) {
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

    public Player getOwnerPlayer() {
        return ownerPlayer;
    }
}
