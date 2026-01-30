package graphics;

import blocks.Block;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import world.BlockRegistry;
import world.Chunk;

import java.util.Map;

public class ChunkMesh {

    private final int CHUNK_SIZE;
    private final int MAX_HEIGHT;
    private final BlockRegistry registry;
    private final TextureAtlas atlas;

    public ChunkMesh(int size, int maxHeight, BlockRegistry registry, TextureAtlas atlas) {
        this.CHUNK_SIZE = size;
        this.MAX_HEIGHT = maxHeight;
        this.registry = registry;
        this.atlas = atlas;
    }

    public MeshData generateChunkMesh(Map<Long, Chunk> chunks, int[] blockIDs, int chunkX, int chunkZ) {

        // cache ids to avoid repeated lookups
        final int AIR_ID = registry.idFromName("air_block");
        final int WATER_ID = -1;//registry.idFromName("water_block");

        FloatArrayList waterVertList = new FloatArrayList();
        IntArrayList waterIdxList = new IntArrayList();
        int waterFaces = 0;

        FloatArrayList solidVertList = new FloatArrayList();
        IntArrayList solidIdxList = new IntArrayList();
        int solidFaces = 0;

        for(int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < MAX_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {

                    int wx = CHUNK_SIZE * chunkX + x;
                    int wz = CHUNK_SIZE * chunkZ + z;

                    int blockID = blockIDs[x + y * CHUNK_SIZE + z * CHUNK_SIZE * MAX_HEIGHT];
                    if(blockID == AIR_ID)
                        continue;

                    boolean isWater = false; // (blockID == WATER_ID);

                    for(int face = 0; face < 6; face++) {
                        int dx = BlockMesh.NEIGHBOR[face][0];
                        int dy = BlockMesh.NEIGHBOR[face][1];
                        int dz = BlockMesh.NEIGHBOR[face][2];

                        Block neighbor = getBlockWorld(chunks, blockIDs, wx + dx, y + dy, wz + dz, chunkX, chunkZ);

                        // safety: if registry returned null for some reason, treat as air
                        if (neighbor == null) neighbor = registry.blockFromName("air_block");

                        if(isWater) {
                            if(neighbor.getID() == WATER_ID) continue;
                            if(!neighbor.isTransparent()) continue;
                        } else {
                            if(!neighbor.isTransparent() && neighbor.getID() != WATER_ID) continue;
                        }

                        if(isWater) {
                            addFace(waterVertList, waterIdxList, x, y, z, waterFaces, blockID, face);
                            waterFaces++;
                        } else {
                            addFace(solidVertList, solidIdxList, x, y, z, solidFaces, blockID, face);
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

    private Block getBlockWorld(Map<Long, Chunk> chunks, int[] blockIDs,
                                int wx, int wy, int wz,
                                int chunkX, int chunkZ)
    {
        // out-of-range Y -> air
        if (wy < 0 || wy >= MAX_HEIGHT) {
            return registry.blockFromName("air_block");
        }

        int cx = Math.floorDiv(wx, CHUNK_SIZE);
        int cz = Math.floorDiv(wz, CHUNK_SIZE);

        int lx = Math.floorMod(wx, CHUNK_SIZE);
        int lz = Math.floorMod(wz, CHUNK_SIZE);

        if(cx == chunkX && cz == chunkZ) {
            int id = blockIDs[lx + wy * CHUNK_SIZE + lz * CHUNK_SIZE * MAX_HEIGHT];
            Block b = registry.blockFromID(id);
            return b != null ? b : registry.blockFromName("air_block");
        }

        Chunk neighbor = chunks.get(getChunkID(cx, cz));
        if(neighbor == null) return registry.blockFromName("air_block");

        int nid = neighbor.getBlockID(lx, wy, lz);
        Block nb = registry.blockFromID(nid);
        return nb != null ? nb : registry.blockFromName("air_block");
    }

    private void addFace(FloatArrayList verts, IntArrayList indices,
                         float dx, float dy, float dz,
                         int addedFaces,
                         int blockID,
                         int face)
    {
        Block b = registry.blockFromID(blockID);

        float[] uv = atlas.getUVForFace(b.getTextureID(face));
        float[] n  = BlockMesh.FACE_NRM[face];
        float[][] p = BlockMesh.FACE_POS[face];

        for (int v = 0; v < 4; v++) {
            verts.add(p[v][0] + dx);
            verts.add(p[v][1] + dy);
            verts.add(p[v][2] + dz);

            verts.add(n[0]);
            verts.add(n[1]);
            verts.add(n[2]);

            verts.add(uv[v*2]);
            verts.add(uv[v*2 + 1]);
        }

        for (int i = 0; i < 6; i++) {
            indices.add(BlockMesh.FACE_IDX[i] + 4 * addedFaces);
        }
    }

    private long getChunkID(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) ^ (chunkZ & 0xffffffffL);
    }
}
