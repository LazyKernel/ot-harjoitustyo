package asteroids.core.graphics.ui;

import asteroids.core.graphics.shaders.Shader;

public class UIShader extends Shader {
    public void init() {
        createShader("src/main/resources/ui_vert.glsl", "src/main/resources/ui_frag.glsl");
    }
}
