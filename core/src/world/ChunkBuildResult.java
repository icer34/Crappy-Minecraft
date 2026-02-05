package world;

import graphics.ChunkMeshData;

public record ChunkBuildResult(int chunkX, int chunkZ, int[] blocks, ChunkMeshData data) {}
