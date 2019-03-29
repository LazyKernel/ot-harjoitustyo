package asteroids.core.game;

import asteroids.core.game.components.PlayerMesh;
import asteroids.core.graphics.Renderer;

public class Game
{
    private Renderer renderer;

    public void init()
    {
        Entity player = new Entity();
        PlayerMesh mesh = new PlayerMesh();
        player.addComponent(mesh);
        renderer.addEntity(player);
    }

    public void update()
    {

    }

    public void render()
    {

    }

    public void destroy()
    {

    }

    public void setRenderer(Renderer renderer)
    {
        this.renderer = renderer;
    }
}
