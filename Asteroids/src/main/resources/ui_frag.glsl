#version 330

precision mediump float;
uniform sampler2D tex;

in vec2 fragUV;
in vec4 fragColor;
out vec4 outColor;

void main() {
    outColor = fragColor * texture(tex, fragUV.st);
}
