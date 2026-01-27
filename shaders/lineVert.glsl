#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projMatrix * viewMatrix * mat4(1.0f) * vec4(aPos, 1.0f);
}