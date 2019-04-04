package asteroids.core.game.components;

import asteroids.core.game.EntityComponent;
import asteroids.core.graphics.Mesh;
import org.joml.Vector3f;

public class Bullet extends EntityComponent
{
    private static final Vector3f[] POINTS = new Vector3f[]{
            new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)
    };

    Mesh bulletMesh;

    @Override
    public void init()
    {

    }

    @Override
    public void render()
    {

    }

    @Override
    public void update(float deltaTime)
    {

    }

    @Override
    public void destroy()
    {

    }
}
