#version 330 core

//data format:
layout (location = 0) in uint aData;
layout (location = 1) in uint aData2;

//uniforms needed to compute uv coords from textureID
uniform float texture_padding;
uniform float texture_size;
uniform float atlas_size;
uniform float slots_per_row;

//transformation matrices
uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;

//uniform float time;

//vertex positions for every face/corner of a cube
const vec3 POS[24] = vec3[24](
// face 0 north (-z)
vec3(0,0,0), vec3(0,1,0), vec3(1,1,0), vec3(1,0,0),

// face 1 south (+z)
vec3(1,0,1), vec3(1,1,1), vec3(0,1,1), vec3(0,0,1),

// face 2 east (+x)
vec3(1,0,0), vec3(1,1,0), vec3(1,1,1), vec3(1,0,1),

// face 3 west (-x)
vec3(0,0,1), vec3(0,1,1), vec3(0,1,0), vec3(0,0,0),

// face 4 top (+y)
vec3(0,1,1), vec3(1,1,1), vec3(1,1,0), vec3(0,1,0),

// face 5 bottom (-y)
vec3(0,0,0), vec3(1,0,0), vec3(1,0,1), vec3(0,0,1)
);

//normals for every respective face
const vec3 NORMALS[6] = vec3[6](
vec3(0,0,-1),   //face 0
vec3(0,0,1),    //face 1
vec3(1,0,0),    //face 2
vec3(-1,0,0),   //face 3
vec3(0,1,0),    //face 4
vec3(0,-1,0)    //face 5
);

//outputs of the shader
out vec3 vNorm;
out vec2 vUV;
out vec3 vWorldPos;

void main() {
    //unpack data
    uint x = (aData >> 28) & 0xFu;
    uint y = (aData >> 19) & 0x1FFu;
    uint z = (aData >> 15) & 0xFu;
    uint face = (aData >> 12) & 0x7u;
    uint corner = (aData >> 10) & 0x3u;
    uint textureID = aData & 0x3FFu;
    uint ovrTextureID = (aData2 >> 6) & 0x3FFu;
    uint flags = aData2 & 0x3Fu;

    //uv coords computation
    uint slotx = textureID % uint(slots_per_row);
    uint sloty = textureID / uint(slots_per_row);

    int px = int(slotx * (texture_size + texture_padding) + (texture_padding / 2));
    int py = int(sloty * (texture_size + texture_padding) + (texture_padding / 2));

    float inv = 1.0f / atlas_size;
    float u0 = (px) * inv;
    float v0 = (py) * inv;
    float u1 = (px + texture_size) * inv;
    float v1 = (py + texture_size) * inv;

    if(corner == 0u)
        vUV = vec2(u0, v1);
    else if(corner == 1u)
        vUV = vec2(u0, v0);
    else if(corner == 2u)
        vUV = vec2(u1, v0);
    else if(corner == 3u)
        vUV = vec2(u1, v1);


    vNorm = NORMALS[face];

    uint idx = 4u * face + corner;
    vec3 pos = vec3(x, y, z) + POS[idx];
    pos.y -= 0.15;

    vWorldPos = (worldMatrix * vec4(pos, 1.0f)).xyz;
    gl_Position = projMatrix * viewMatrix * worldMatrix * vec4(pos, 1.0f);
}