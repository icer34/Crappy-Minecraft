#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNorm;
layout (location = 2) in vec2 aUV;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;
uniform float time;

out vec3 vNorm;
out vec2 vUV;
out vec3 vWorldPos;

void main() {
    vUV = aUV;

    vec4 worldPos = worldMatrix * vec4(aPos, 1.0f);

    //lower water lvl
    worldPos.y -= 0.15f;

    worldPos.y += (sin(aPos.x * 3.14156592 / 2 + time) +
                   sin(aPos.z * 3.14156592 / 2 + time * 1.5f)) * 0.05f;

    vWorldPos = worldPos.xyz;
    vNorm = mat3(worldMatrix) * aNorm;

    gl_Position = projMatrix * viewMatrix * worldPos;
}