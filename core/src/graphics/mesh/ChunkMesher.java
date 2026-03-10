package graphics.mesh;

import game.blocks.Block;
import game.blocks.GrassBlock;
import game.blocks.MultiTexturedBlock;
import game.blocks.WaterBlock;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import game.blocks.BlockRegistry;
import game.world.Chunk;

import java.util.Map;

public class ChunkMesher {

    private final int CHUNK_SIZE;
    private final int MAX_HEIGHT;
    private final BlockRegistry registry;

    public ChunkMesher(int size, int maxHeight, BlockRegistry registry) {
        this.CHUNK_SIZE = size;
        this.MAX_HEIGHT = maxHeight;
        this.registry = registry;
    }

    public ChunkMeshData generateChunkMesh(Map<Long, Chunk> chunks, int[] blockIDs, int chunkX, int chunkZ, boolean isPacked) {

        // cache ids to avoid repeated lookups
        final int AIR_ID = registry.idFromName("air_block");
        final int WATER_ID = registry.idFromName("water_block");

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

                    boolean isWater = (blockID == WATER_ID);

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
                            if(isPacked) addPackedSolidFace(waterVertList, waterIdxList, x, y, z, waterFaces, blockID, face);
                            waterFaces++;
                        } else {
                            if(isPacked) addPackedSolidFace(solidVertList, solidIdxList, x, y, z, solidFaces, blockID, face);
                            solidFaces++;
                        }
                    }
                }
            }
        }

        return new ChunkMeshData(
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

    private void addPackedSolidFace(FloatArrayList verts, IntArrayList indices,
                              float x, float y, float z,
                              int addedFaces,
                              int blockID,
                              int face)
    {
        //we pack data of a vertex in a 32-bit integer + a 16-bit short
        //data format: x - y - z - faceIdx - cornerIdx - textureID -> in bits: 4 - 9 - 4 - 3 - 2 - 10 = 32
        //             textureOverlayID - tintIdx - flags -> 10 - 3 - 19 = 32

        Block b = registry.blockFromID(blockID);
        int textureID = b.getTextureID(face);
        int ovrTextureID = -1;
        if(b instanceof MultiTexturedBlock mt) ovrTextureID = mt.getOvrTextureID(face);
        int tintIdx = -1;
        if(b instanceof GrassBlock) tintIdx = 0;
        else if(b instanceof WaterBlock) tintIdx = 1;
        int flags = 0;

        for(int corner = 0; corner < 4; corner++) {
            int data1 = ((int) x << 28) |
                       ((int) y << 19) |
                       ((int) z << 15) |
                       (face << 12)    |
                       (corner << 10)  |
                       (textureID);

            int data2 = ((ovrTextureID << 22) |
                         (tintIdx << 19) |
                          (flags));

            verts.add(Float.intBitsToFloat(data1));
            verts.add(Float.intBitsToFloat(data2));
        }

        for(int i = 0; i < 6; i++) {
            indices.add(BlockMesh.FACE_IDX[i] + 4 * addedFaces);
        }
    }

    private void addPackedWaterFace(FloatArrayList verts, IntArrayList indices,
                              float x, float y, float z,
                              int addedFaces,
                              int blockID,
                              int face)
    {
        //we pack data differently for water meshes and solid meshes
        //for a water vertex we need x, y, z, nx, ny, nz, baseTextureID, textureIDOffset, flags

        //TODO
    }

    private long getChunkID(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) ^ (chunkZ & 0xffffffffL);
    }
}
