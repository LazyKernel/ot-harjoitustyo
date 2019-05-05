package asteroids.core.graphics.ui;

import asteroids.core.graphics.shaders.Shader;

/**
 * Shader for UI Elements
 */
public class UIShader extends Shader {
    /**
     * Loads and creates the shader
     * @see Shader
     */
    public void init() {
        createShader("src/main/resources/ui_vert.glsl", "src/main/resources/ui_frag.glsl");
    }
}
