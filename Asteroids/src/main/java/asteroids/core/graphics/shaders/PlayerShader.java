package asteroids.core.graphics.shaders;

public class PlayerShader extends Shader
{
    public void init()
    {
        createShader("src/main/resources/player_vert.glsl", "src/main/resources/player_frag.glsl");
    }
}
