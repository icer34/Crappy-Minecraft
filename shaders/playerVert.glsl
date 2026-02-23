#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNorm;
layout (location = 2) in vec2 aUV;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;

void main() {
    gl_Position = projMatrix * viewMatrix * worldMatrix * vec4(aPos, 1.0f);
}