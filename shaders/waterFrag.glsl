#version 330 core

in vec3 vNorm;
in vec2 vUV;
in vec3 vWorldPos;

uniform vec3 lightDir;
uniform vec3 lightColor;
uniform float ambientStrength;

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

    vec3 N = normalize(vNorm);
    vec3 L = normalize(-lightDir);

    float ndotl = max(dot(N, L), 0.0);

    float sun = saturate(-lightDir.y);

    float sunAtten = smoothstep(0.1, 0.9, sun);

    float diffuseStrength = mix(0.20, 1.0, ndotl) * sunAtten;

    vec3 ambient = 0.1 + ambientStrength * sunAtten * lightColor;
    vec3 diffuse = diffuseStrength * lightColor;

    vec3 lighting = ambient + diffuse;
    lighting = min(lighting, vec3(1.0));

    vec3 color = albedo * lighting;

    if(isUnderWater == 1) {
        float dist = distance(camPos, vWorldPos);

        float fogFactor = 1.0f - exp(-dist * fogDensity);
        fogFactor = clamp(fogFactor, 0.0f, 1.0f);

        color = mix(color, fogColor, fogFactor);
    }

    FragColor = vec4(color, waterTransparency);
}
