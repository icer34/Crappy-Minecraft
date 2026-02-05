#version 330 core

in vec3 vNorm;
in vec2 vBaseUV;
in vec2 vOvrUV;
in vec3 vWorldPos;

uniform vec3 camPos;
uniform vec3 fogColor;
uniform int isUnderWater;
uniform float fogDensity;

uniform sampler2D texture_sampler;
out vec4 FragColor;

float saturate(float x) { return clamp(x, 0.0, 1.0); }

void main() {
    vec4 base = texture(texture_sampler, vBaseUV);
    vec4 overlay = texture(texture_sampler, vOvrUV);

    vec3 albedo = mix(base, overlay, overlay.a).rgb;

    vec3 color = albedo;

    if(isUnderWater == 1) {
        float dist = distance(camPos, vWorldPos);

        float fogFactor = 1.0f - exp(-dist * fogDensity);
        fogFactor = clamp(fogFactor, 0.0f, 1.0f);

        color = mix(color, fogColor, fogFactor);
    }

    vec3 final = color;
    FragColor = vec4(color, 1.0);
}
