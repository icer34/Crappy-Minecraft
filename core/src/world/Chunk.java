package world;

import graphics.BiomeMapTexture;
import graphics.ChunkMeshData;
import graphics.PackedMesh;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Chunk {

    private final int SIZE;
    private final int MAX_HEIGHT;

    private int chunkX, chunkZ;

    int[] blocks;

    byte[][] biomeMap;
    private boolean biomeMapDirty = true;
    private final BiomeMapTexture biomeMapTexture;

    private boolean isVisible = true;

    private PackedMesh solidMesh;
    private PackedMesh waterMesh;

    public Chunk(int x, int z, int size, int maxHeight) {
        this.chunkX = x;
        this.chunkZ = z;
        this.SIZE = size;
        this.MAX_HEIGHT = maxHeight;
        this.blocks = new int[SIZE * SIZE * MAX_HEIGHT];
        this.biomeMap = new byte[SIZE][SIZE];
        this.solidMesh = new PackedMesh();
        this.waterMesh = new PackedMesh();
        this.biomeMapTexture = new BiomeMapTexture();
    }

    public void renderSolid() {
        solidMesh.draw();
    }

    public void renderWater() {
        waterMesh.draw();
    }

    public int getBlockID(int x, int y, int z) {
        if(!isValidCoord(x, y, z)) {
            return -1;
        }
        return blocks[getIdx(x, y, z)];
    }

    public void setBlock(Vector3i localPos, int blockID) {
        if(!isValidCoord(localPos.x, localPos.y, localPos.z))
            throw new RuntimeException("Trying to set block to illegal coords: " + localPos);
        blocks[getIdx(localPos.x, localPos.y, localPos.z)] = blockID;
    }

    public void setBiomeMap(byte[][] biomeMap) {
        this.biomeMap = biomeMap;
    }

    public Biome getBiomeAt(int lx, int lz) {
        return Biome.fromID(biomeMap[lx][lz]);
    }

    public byte[][] getBiomeMap() {
        return biomeMap;
    }

    private boolean isValidCoord(int x, int y, int z) {
        return x >= 0 && x < SIZE &&
                y >= 0 && y < MAX_HEIGHT &&
                z >= 0 && z < SIZE;
    }

    private int getIdx(int x, int y, int z) {
        if(!isValidCoord(x, y, z)) {
            throw new IndexOutOfBoundsException("Coords out of bounds: " + x + " " + y + " " + z);
        }

        return x + y * SIZE + z * SIZE * MAX_HEIGHT;
    }

    public Vector3f getWorldPos() {
        return new Vector3f(chunkX * SIZE, 0.0f, chunkZ * SIZE);
    }

    public void setBlockIDs(int[] blocks) {
        this.blocks = blocks;
    }

    public void cleanup() {
        solidMesh.delete();
    }

    public void updateMeshes(ChunkMeshData data) {
        solidMesh.update(data.solidVert(), data.solidIdx());
        waterMesh.update(data.waterVert(), data.waterIdx());
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int[] getBlocks() {
        return blocks;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int getSize() {
        return SIZE;
    }

    public int getMaxHeight() {
        return MAX_HEIGHT;
    }

    public void bindBiomeMapTexture() {
        this.biomeMapTexture.bind();
    }

    public void updateBiomeMapTexture() {
        if(!biomeMapDirty) return;

        biomeMapTexture.update(this);
        biomeMapDirty = false;
    }
}
