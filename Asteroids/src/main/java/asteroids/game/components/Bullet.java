package asteroids.game.components;

import asteroids.core.containers.Transform;
import asteroids.core.graphics.Mesh;
import asteroids.core.networking.INetworked;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class Bullet extends INetworked {
    private static final Vector3f[] POINTS = new Vector3f[]{ new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f) };
    private Mesh bulletMesh;

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

    @Override
    public void netSerialize(List<Object> objects, boolean isServer) {
        if (isServer) {
            objects.add(getTransform());
        }
    }

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
}
