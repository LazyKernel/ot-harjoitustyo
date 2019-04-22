#version 430

precision mediump float;
layout (location = 4) uniform sampler2D tex;

in vec2 fragUV;
in vec4 fragColor;
out vec4 outColor;

void main() {
    outColor = fragColor * texture(tex, fragUV.st);
}
