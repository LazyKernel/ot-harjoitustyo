package asteroids.game.components;

import asteroids.game.EntityComponent;
import asteroids.core.graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_LINE;

public class Bullet extends EntityComponent
{
    private static final Vector3f[] POINTS = new Vector3f[]{
            new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)
    };

    private Mesh bulletMesh;

    @Override
    public void init()
    {
        bulletMesh = new Mesh();
        bulletMesh.setPoints(POINTS, GL_LINE);
        getTransform().setScale(new Vector2f(10, 10));
        getParent().addComponent(bulletMesh);
    }

    @Override
    public void render()
    {

    }

    @Override
    public void update(float deltaTime)
    {
        Vector3f forward = new Vector3f(0.0f, 1.0f, 0.0f);
        forward.rotateZ(getTransform().getRotation());
        getTransform().translate(forward.mul(deltaTime));
    }

    @Override
    public void destroy()
    {

    }
}
