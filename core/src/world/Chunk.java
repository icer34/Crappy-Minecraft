package world;

import blocks.*;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Chunk {

    private final int SIZE;
    private final int MAX_HEIGHT;

    private int chunkX, chunkZ;

    int[] blocks;

    private boolean isVisible = true;

    private float[] solidVert = new float[]{};
    private int[] solidIdx = new int[] {};
    private int solidVao;
    private int solidEbo;
    private int solidVbo;
    private int numSolidVert;

    private float[] waterVert = new float[]{};
    private int[] waterIdx = new int[]{};
    private int waterVao;
    private int waterEbo;
    private int waterVbo;
    private int numWaterVert;

    public Chunk(int x, int z, int size, int maxHeight) {
        this.chunkX = x;
        this.chunkZ = z;
        this.SIZE = size;
        this.MAX_HEIGHT = maxHeight;
        this.blocks = new int[SIZE * SIZE * MAX_HEIGHT];
    }

    public void renderSolid() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glEnable(GL_DEPTH_TEST);

        glBindVertexArray(solidVao);
        glDrawElements(GL_TRIANGLES, numSolidVert, GL_UNSIGNED_INT, 0);
    }

    public void renderWater() {
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDepthMask(false);
        glDepthFunc(GL_LEQUAL);

        glBindVertexArray(waterVao);
        glDrawElements(GL_TRIANGLES, numWaterVert, GL_UNSIGNED_INT, 0);

        glDepthMask(true);
        glDisable(GL_BLEND);
        glDepthFunc(GL_LESS);
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

    public void updateBuffers() {

        //========== WATER BLOCKS BUFFERS ===========
        FloatBuffer waterVertBuffer = MemoryUtil.memAllocFloat(waterVert.length);
        waterVertBuffer.put(waterVert).flip();
        IntBuffer waterIdxBuffer = MemoryUtil.memAllocInt(waterIdx.length);
        waterIdxBuffer.put(waterIdx).flip();

        this.waterVao = glGenVertexArrays();
        glBindVertexArray(waterVao);

        this.waterVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, waterVbo);
        glBufferData(GL_ARRAY_BUFFER, waterVertBuffer, GL_STATIC_DRAW);

        this.waterEbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, waterEbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, waterIdxBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);

        //========== SOLID BLOCKS BUFFERS ===========
        FloatBuffer solidVertBuffer = MemoryUtil.memAllocFloat(solidVert.length);
        solidVertBuffer.put(solidVert).flip();
        IntBuffer solidIdxBuffer = MemoryUtil.memAllocInt(solidIdx.length);
        solidIdxBuffer.put(solidIdx).flip();

        this.solidVao = glGenVertexArrays();
        glBindVertexArray(solidVao);

        this.solidVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, solidVbo);
        glBufferData(GL_ARRAY_BUFFER, solidVertBuffer, GL_STATIC_DRAW);

        this.solidEbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, solidEbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, solidIdxBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);

        //Free memory
        MemoryUtil.memFree(solidVertBuffer);
        MemoryUtil.memFree(solidIdxBuffer);
        MemoryUtil.memFree(waterVertBuffer);
        MemoryUtil.memFree(waterIdxBuffer);
    }

    public void setBlockIDs(int[] blocks) {
        this.blocks = blocks;
    }

    public void cleanup() {
        glBindVertexArray(0);
        glDeleteVertexArrays(solidVao);

        glDeleteBuffers(solidVbo);
        glDeleteBuffers(solidEbo);
    }

    public void setSolidMeshData(float[] vertices, int[] indices) {
        this.solidVert = vertices;
        this.solidIdx = indices;
        this.numSolidVert = indices.length;
    }

    public void setWaterMeshData(float[] vertices, int[] indices) {
        this.waterVert = vertices;
        this.waterIdx = indices;
        this.numWaterVert = indices.length;
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
}
