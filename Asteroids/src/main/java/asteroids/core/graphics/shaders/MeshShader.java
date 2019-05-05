package asteroids.core.graphics.shaders;

/**
 * Basic mesh shader
 */
public class MeshShader extends Shader {
    /**
     * Initialize the shader
     */
    public void init() {
        createShader("src/main/resources/mesh_vert.glsl", "src/main/resources/mesh_frag.glsl");
    }
}
