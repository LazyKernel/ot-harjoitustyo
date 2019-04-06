package asteroids.core.graphics.shaders;

public class MeshShader extends Shader {
    public void init() {
        createShader("src/main/resources/mesh_vert.glsl", "src/main/resources/mesh_frag.glsl");
    }
}
