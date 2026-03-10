package game.world;

import graphics.mesh.ChunkMeshData;

public record ChunkBuildResult(int chunkX, int chunkZ, int[] blocks, ChunkMeshData data) {}
