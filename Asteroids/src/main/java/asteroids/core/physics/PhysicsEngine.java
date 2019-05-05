package asteroids.core.physics;

import asteroids.core.containers.ModifiableList;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;

/**
 * Physics engine using GJK for hit detection
 *
 * Continuous collision detection not implemented
 */
public class PhysicsEngine {
    private ModifiableList<ICollider> colliders = new ModifiableList<>();

    public PhysicsEngine() {

    }

    private Vector2f support(ICollider c1, ICollider c2, Vector2f dir) {
        Vector3f dir3d = new Vector3f(dir, 0.0f);
        Vector2f p1 = c1.getFarthestPoint(dir3d);
        Vector2f p2 = c2.getFarthestPoint(dir3d.negate());
        return p1.sub(p2);
    }

    private boolean checkCollision(ICollider c1, ICollider c2) {
        ArrayList<Vector2f> simplex = new ArrayList<>();
        Vector2f d = new Vector2f(0, 1);
        Vector2f point = support(c1, c2, d);

        if (point == null) {
            return false;
        }

        simplex.add(point);

        d = d.negate();

        while (true) {
            simplex.add(support(c1, c2, d));

            if (simplex.get(simplex.size() - 1).dot(d) <= 0) {
                return false;
            } else {
                if (containsOrigin(d, simplex)) {
                    return true;
                }
            }
        }
    }

    private Vector2f tripleProduct(Vector2f a, Vector2f b, Vector2f c) {
        Vector3f res = new Vector3f(a, 0.0f).cross(new Vector3f(b, 0.0f));
        res = res.cross(new Vector3f(c, 0.0f));
        return new Vector2f(res.x, res.y);
    }

    private boolean containsOrigin(Vector2f d, List<Vector2f> simplex) {
        Vector2f a = simplex.get(simplex.size() - 1);
        Vector2f ao = new Vector2f();
        a.negate(ao);

        if (simplex.size() == 3) {
            Vector2f b = simplex.get(simplex.size() - 2);
            Vector2f c = simplex.get(simplex.size() - 3);
            Vector2f ab = b.sub(a);
            Vector2f ac = c.sub(a);

            Vector2f abPerp = tripleProduct(ac, ab, ab);
            Vector2f acPerp = tripleProduct(ab, ac, ac);

            if (abPerp.dot(ao) > 0) {
                simplex.remove(c);
                d.set(abPerp);
            } else {
                if (acPerp.dot(ao) > 0) {
                    simplex.remove(b);
                    d.set(acPerp);
                } else {
                    return true;
                }
            }
        } else {
            Vector2f b = simplex.get(simplex.size() - 2);
            Vector2f ab = b.sub(a);
            Vector2f abPerp = tripleProduct(ab, ao, ab);
            d.set(abPerp);
        }

        return false;
    }

    private Vector2i getTileCoords(Vector2f pos) {
        return new Vector2i((int) (pos.x / 20), (int) (pos.y / 20));
    }

    private void broadPhase(List<Map.Entry<ICollider, ICollider>> collisions) {
        HashMap<Vector2i, List<ICollider>> tiles = new HashMap<>();

        for (ICollider c : colliders) {
            if (c == null) {
                continue;
            }

            Vector2f[] bbox = c.getBoundingBox();
            Vector2i start = getTileCoords(bbox[2]);
            Vector2i end = getTileCoords(bbox[1]);

            ArrayList<ICollider> collidedWith = new ArrayList<>();
            for (int x = start.x; x <= end.x; x++) {
                for (int y = start.y; y <= end.y; y++) {
                    Vector2i pos = new Vector2i(x, y);
                    List<ICollider> tile = tiles.get(pos);

                    if (tile != null) {
                        for (ICollider coll : tile) {
                            if (!collidedWith.contains(coll)) {
                                collisions.add(new AbstractMap.SimpleEntry<>(c, coll));
                                collidedWith.add(coll);
                            }
                        }

                        tile.add(c);
                    } else {
                        List<ICollider> list = new ArrayList<>();
                        list.add(c);
                        tiles.put(pos, list);
                    }
                }
            }
        }
    }

    /**
     * Check for collisions. Runs a broad phase based on a 20x20 grid (arguably too small) and narrow phase using GJK.
     * @param deltaTime seconds since last update
     * @param isServer is this a server
     */
    public void update(float deltaTime, boolean isServer) {
        ArrayList<Map.Entry<ICollider, ICollider>> collisions = new ArrayList<>();
        broadPhase(collisions);

        for (Map.Entry<ICollider, ICollider> c : collisions) {
            if (checkCollision(c.getKey(), c.getValue())) {
                c.getKey().hit(c.getValue(), isServer);
                c.getValue().hit(c.getKey(), isServer);
            }
        }
    }

    public void addCollider(ICollider collider) {
        colliders.add(collider);
    }

    public void removeCollider(ICollider collider) {
        colliders.remove(collider);
    }
}
