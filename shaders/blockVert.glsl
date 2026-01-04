#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNorm;
layout (location = 2) in vec2 aUV;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;

out vec3 vNorm;
out vec2 vUV;
out vec3 vWorldPos;

void main() {
    vUV = aUV;

    vec4 worldPos = worldMatrix * vec4(aPos, 1.0f);
    vWorldPos = worldPos.xyz;

    vNorm = mat3(worldMatrix) * aNorm;

    gl_Position = projMatrix * viewMatrix * worldPos;
}