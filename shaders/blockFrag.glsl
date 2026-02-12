#version 330 core

in vec3 vNorm;
in vec2 vBaseUV;
in vec2 vOvrUV;
in vec3 vWorldPos;
in vec2 vLocalPos;
flat in uint vTintIdx;

uniform vec3 camPos;
uniform vec3 fogColor;
uniform int isUnderWater;
uniform float fogDensity;

uniform sampler2D textureAtlas;
uniform usampler2D biomeMapTex;
uniform sampler2D tintTexture;

out vec4 FragColor;

float saturate(float x) { return clamp(x, 0.0, 1.0); }

void main() {
    vec4 base = texture(textureAtlas, vBaseUV);
    vec4 overlay = texture(textureAtlas, vOvrUV);
    float overlayAlpha = overlay.a;
    uint biomeID = texelFetch(biomeMapTex, ivec2(vLocalPos.xy), 0).r;

    vec3 overlayColor;
    if (vTintIdx != -1u) {
        vec3 tintColor = texelFetch(
            tintTexture,
            ivec2(int(biomeID), int(vTintIdx)),
            0
        ).rgb;
        overlayColor = overlay.rgb * tintColor;
    } else {
        overlayColor = overlay.rgb;
    }

    vec3 albedo = mix(base, vec4(overlayColor, overlayAlpha), overlayAlpha).rgb;

    vec3 color = albedo;

    if(isUnderWater == 1) {
        float dist = distance(camPos, vWorldPos);

        float fogFactor = 1.0f - exp(-dist * fogDensity);
        fogFactor = clamp(fogFactor, 0.0f, 1.0f);

        color = mix(color, fogColor, fogFactor);
    }

    FragColor = vec4(color, 1.0);
}
