package world;

import graphics.MeshData;

public record ChunkBuildResult(int chunkX, int chunkZ, int[] blocks, MeshData data) {}
