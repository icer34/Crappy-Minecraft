package graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

public class PackedMesh implements Mesh{

    private final int vao;
    private final int vbo;
    private final int ebo;

    private int numVert;
    private int numIdx;

    public PackedMesh() {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
    }

    @Override
    public void update(float[] vertices, int[] indices) {
        numVert = vertices.length;
        numIdx = indices.length;

        glBindVertexArray(vao);

        FloatBuffer fb = MemoryUtil.memAllocFloat(numVert);
        fb.put(vertices).flip();
        IntBuffer ib = MemoryUtil.memAllocInt(numIdx);
        ib.put(indices).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);

        glVertexAttribIPointer(0, 1, GL_UNSIGNED_INT, 2 * Integer.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribIPointer(1, 1, GL_UNSIGNED_SHORT, 2 * Integer.BYTES, Integer.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_VERTEX_ARRAY, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(fb);
        MemoryUtil.memFree(ib);
    }

    @Override
    public void draw() {
        glBindVertexArray(vao);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDrawElements(GL_TRIANGLES, numVert, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void delete() {
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }
}
