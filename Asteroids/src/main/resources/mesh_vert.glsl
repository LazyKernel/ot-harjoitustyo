#version 330

in vec3 vertPos;

uniform vec3 customColor;
uniform mat4 transform;

out vec3 color;

void main() {
    color = customColor;
    gl_Position = transform * vec4(vertPos, 1.0);
}