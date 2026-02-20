#version 330 core

in vec3 vNorm;
in vec2 vUV;
in vec3 vWorldPos;
in vec2 vLocalPos;

uniform vec3 camPos;
uniform vec3 fogColor;
uniform int isUnderWater;
uniform float fogDensity;
uniform float waterTransparency;

uniform sampler2D textureAtlas;
uniform usampler2D biomeMapTex;
uniform sampler2D tintTexture;

out vec4 FragColor;

void main() {
    vec3 albedo = texture(textureAtlas, vUV).rgb;

    vec3 color = albedo;

    uint biomeID = texelFetch(biomeMapTex, ivec2(vLocalPos.xy), 0).r;
    vec3 tintColor = texelFetch(
        tintTexture,
        ivec2(int(biomeID), 1),
        0
    ).rgb;

    if(isUnderWater == 1) {
        float dist = distance(camPos, vWorldPos);

        float fogFactor = 1.0f - exp(-dist * fogDensity);
        fogFactor = clamp(fogFactor, 0.0f, 1.0f);

        color = mix(color, fogColor, fogFactor);
    }

    FragColor = vec4(color * tintColor, waterTransparency);
}
