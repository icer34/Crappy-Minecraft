package graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL33.*;

public class HUDMesh implements Mesh {

    private final int vao;
    private final int vbo;
    private final int ebo;

    private int numVert;
    private int numIdx;

    public HUDMesh() {
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
    }

    @Override
    public void update(MeshData data) {
        numVert = data.vertices().length;
        numIdx = data.indices().length;

        FloatBuffer fb = MemoryUtil.memAllocFloat(numVert);
        fb.put(data.vertices()).flip();
        IntBuffer ib = MemoryUtil.memAllocInt(numIdx);
        ib.put(data.indices()).flip();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ib, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

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
        glDeleteVertexArrays(vao);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
}
