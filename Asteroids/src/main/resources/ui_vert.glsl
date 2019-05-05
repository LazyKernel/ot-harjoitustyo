#version 330

uniform mat4 projMatrix;

in vec2 pos;
in vec2 texCoord;
in vec4 color;

out vec2 fragUV;
out vec4 fragColor;

void main() {
    fragUV = texCoord;
    fragColor = color;
    gl_Position = projMatrix * vec4(pos.xy, 0, 1);
}
