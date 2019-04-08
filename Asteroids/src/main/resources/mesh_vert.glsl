#version 430

layout (location = 0) in vec3 vertPos;

layout (location = 1) uniform vec3 customColor;
layout (location = 2) uniform mat4 transform;

out vec3 color;

void main() {
    color = customColor;
    gl_Position = transform * vec4(vertPos, 1.0);
}