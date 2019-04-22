#version 430

layout (location = 0) uniform mat4 projMatrix;

layout (location = 1) in vec2 pos;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in vec4 color;

out vec2 fragUV;
out vec4 fragColor;

void main() {
    fragUV = texCoord;
    fragColor = color;
    gl_Position = projMatrix * vec4(pos.xy, 0, 1);
}
