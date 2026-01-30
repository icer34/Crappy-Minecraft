#version 330 core

layout (lines) in;
layout (triangle_strip, max_vertices = 4) out;

uniform vec2 viewport;
uniform float thickness;

void main() {
    vec4 p1 = gl_in[0].gl_Position;
    vec4 p2 = gl_in[1].gl_Position;

    vec2 pos1 = p1.xy / p1.w;
    vec2 pos2 = p2.xy / p2.w;

    vec2 dir = normalize(pos2 - pos1);
    vec2 normal = vec2(-dir.y, dir.x);

    vec2 offset = normal * (thickness / viewport) * 2.0f;

    gl_Position = vec4((pos1 - offset) * p1.w, p1.z, p1.w);
    EmitVertex();

    gl_Position = vec4((pos1 + offset) * p1.w, p1.z, p1.w);
    EmitVertex();

    gl_Position = vec4((pos2 - offset) * p2.w, p2.z, p2.w);
    EmitVertex();

    gl_Position = vec4((pos2 + offset) * p2.w, p2.z, p2.w);
    EmitVertex();

    EndPrimitive();
}