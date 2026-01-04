package world;

import blocks.BlockType;
import graphics.MeshData;

public record ChunkBuildResult(int chunkX, int chunkZ, BlockType[] blocks, MeshData data) {}
