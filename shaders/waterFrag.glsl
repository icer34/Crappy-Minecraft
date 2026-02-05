#version 330 core

in vec3 vNorm;
in vec2 vUV;
in vec3 vWorldPos;

uniform vec3 camPos;
uniform vec3 fogColor;
uniform int isUnderWater;
uniform float fogDensity;
uniform float waterTransparency;

uniform sampler2D texture_sampler;

out vec4 FragColor;

float saturate(float x) { return clamp(x, 0.0, 1.0); }

void main() {
    vec3 albedo = texture(texture_sampler, vUV).rgb;

    vec3 color = albedo;

    if(isUnderWater == 1) {
        float dist = distance(camPos, vWorldPos);

        float fogFactor = 1.0f - exp(-dist * fogDensity);
        fogFactor = clamp(fogFactor, 0.0f, 1.0f);

        color = mix(color, fogColor, fogFactor);
    }

    FragColor = vec4(color, waterTransparency);
}
