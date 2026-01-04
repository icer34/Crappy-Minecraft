package graphics;

import blocks.BlockType;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import world.Chunk;

import java.util.Map;

public class ChunkMesh {

    private final int CHUNK_SIZE;
    private final int MAX_HEIGHT;

    public ChunkMesh(int size, int maxHeight) {
        this.CHUNK_SIZE = size;
        this.MAX_HEIGHT = maxHeight;
    }

    public MeshData generateChunkMesh(Map<Long, Chunk> chunks, BlockType[] blocks, int chunkX, int chunkZ) {

        FloatArrayList waterVertList = new FloatArrayList();
        IntArrayList waterIdxList = new IntArrayList();
        int waterFaces = 0;

        FloatArrayList solidVertList = new FloatArrayList();
        IntArrayList solidIdxList = new IntArrayList();
        int solidFaces = 0;

        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int y = 0; y < MAX_HEIGHT; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {

                    int wx = CHUNK_SIZE * chunkX + x;
                    int wz = CHUNK_SIZE * chunkZ + z;

                    BlockType type = getBlockLocal(blocks, x, y, z);
                    if(type == BlockType.AIR_BLOCK) continue;

                    boolean isWater = (type == BlockType.WATER_BLOCK);

                    for(int face = 0; face < 6; face ++) {
                        int dx = BlockMesh.NEIGHBOR[face][0];
                        int dy = BlockMesh.NEIGHBOR[face][1];
                        int dz = BlockMesh.NEIGHBOR[face][2];

                        BlockType neighbor = getBlockWorld(chunks, blocks, wx + dx, y + dy, wz + dz, chunkX, chunkZ);

                        if(isWater) {
                            if(neighbor == BlockType.WATER_BLOCK) continue;
                            if(!neighbor.transparent) continue;
                        } else {
                            if(!neighbor.transparent && neighbor != BlockType.WATER_BLOCK) continue;
                        }

                        if(isWater) {
                            addFace(type, face, x, y, z, waterVertList, waterIdxList, waterFaces);
                            waterFaces++;
                        } else {
                            addFace(type, face, x, y, z, solidVertList, solidIdxList, solidFaces);
                            solidFaces++;
                        }
                    }
                }
            }
        }

        return new MeshData(
                solidVertList.toFloatArray(), solidIdxList.toIntArray(),
                waterVertList.toFloatArray(), waterIdxList.toIntArray()
        );
    }

    private BlockType getBlockLocal(BlockType[] blocks, int x, int y, int z) {
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= MAX_HEIGHT || z < 0 || z >= CHUNK_SIZE) {
            return BlockType.AIR_BLOCK;
        }
        int idx = x + y * CHUNK_SIZE + z * CHUNK_SIZE * MAX_HEIGHT;
        BlockType t = blocks[idx];
        return (t == null) ? BlockType.AIR_BLOCK : t;
    }

    private void addFace(BlockType type, int face,
                         float offX, float offY, float offZ,
                         FloatArrayList vertList, IntArrayList idxList,
                         int addedFaces) {

        float[] uv = type.getUV(face);
        float[] n  = BlockMesh.FACE_NRM[face];
        float[][] p = BlockMesh.FACE_POS[face];

        for (int v = 0; v < 4; v++) {
            vertList.add(p[v][0] + offX);
            vertList.add(p[v][1] + offY);
            vertList.add(p[v][2] + offZ);

            vertList.add(n[0]);
            vertList.add(n[1]);
            vertList.add(n[2]);

            vertList.add(uv[v*2]);
            vertList.add(uv[v*2 + 1]);
        }

        for (int i = 0; i < 6; i++) {
            idxList.add(BlockMesh.FACE_IDX[i] + 4 * addedFaces);
        }
    }

    public BlockType getBlockWorld(Map<Long, Chunk> chunks, BlockType[] blocks, int wx, int wy, int wz, int chunkX, int chunkZ) {
        int cx = Math.floorDiv(wx, CHUNK_SIZE);
        int cz = Math.floorDiv(wz, CHUNK_SIZE);

        int lx = Math.floorMod(wx, CHUNK_SIZE);
        int lz = Math.floorMod(wz, CHUNK_SIZE);

        if(cx == chunkX && cz == chunkZ) return getBlockLocal(blocks, lx, wy, lz);

        Chunk neighbor = chunks.get(getChunkID(cx, cz));
        if(neighbor == null) return BlockType.AIR_BLOCK;

        return neighbor.getBlock(lx, wy, lz);
    }

    private long getChunkID(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) ^ (chunkZ & 0xffffffffL);
    }
}
