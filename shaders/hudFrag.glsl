#version 330 core

in vec2 fragTexCoord;
out vec4 fragColor;

uniform sampler2D hudTexture;

void main() {
    fragColor = texture(hudTexture, fragTexCoord);
}