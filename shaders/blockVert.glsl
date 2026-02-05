#version 330 core

//data format : packed in a 32 bit integer -> x-y-z-faceIdx-cornerIdx-textureID
//                             value in bits: 4-9-4-   3   -    2    -    10
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
out vec2 vBaseUV;
out vec2 vOvrUV;
out vec3 vWorldPos;

vec4 getUV(uint textureID) {
    uint slotx = textureID % uint(slots_per_row);
    uint sloty = textureID / uint(slots_per_row);

    int px = int(slotx * (texture_size + texture_padding) + (texture_padding / 2));
    int py = int(sloty * (texture_size + texture_padding) + (texture_padding / 2));

    float inv = 1.0f / atlas_size;

    return vec4(px * inv, py * inv, (px + texture_size) * inv, (py + texture_size) * inv);
}

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

    vec4 baseUV = getUV(textureID);
    vec4 ovrUV = getUV(ovrTextureID);

    if(corner == 0u) {
        vBaseUV = vec2(baseUV[0], baseUV[3]);
        vOvrUV = vec2(ovrUV[0], ovrUV[3]);

    } else if(corner == 1u) {
        vBaseUV = vec2(baseUV[0], baseUV[1]);
        vOvrUV = vec2(ovrUV[0], ovrUV[1]);

    } else if(corner == 2u) {
        vBaseUV = vec2(baseUV[2], baseUV[1]);
        vOvrUV = vec2(ovrUV[2], ovrUV[1]);

    } else if(corner == 3u) {
        vBaseUV = vec2(baseUV[2], baseUV[3]);
        vOvrUV = vec2(ovrUV[2], ovrUV[3]);

    }

    uint idx = 4u * face + corner;
    vec3 pos = vec3(x, y, z) + POS[idx];
    vNorm = NORMALS[face];
    vWorldPos = (worldMatrix * vec4(pos, 1.0f)).xyz;
    gl_Position = projMatrix * viewMatrix * worldMatrix * vec4(pos, 1.0f);
}