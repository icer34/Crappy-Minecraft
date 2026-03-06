package graphics.mesh;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

public class StandardMesh implements Mesh {

    private final int vao;
    private final int vbo;
    private final int ebo;

    private int numVert;
    private int  numIdx;

    public StandardMesh() {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
    }

    @Override
    public void update(MeshData data) {
        float[] vertices = data.vertices();
        int[] indices = data.indices();

        this.numVert = vertices.length;
        this.numIdx = indices.length;

        FloatBuffer fb = MemoryUtil.memAllocFloat(numVert);
        fb.put(vertices).flip();
        IntBuffer ib = MemoryUtil.memAllocInt(numIdx);
        ib.put(indices).flip();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);

        MemoryUtil.memFree(fb);
        MemoryUtil.memFree(ib);
    }

    @Override
    public void draw() {
        glBindVertexArray(vao);
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
