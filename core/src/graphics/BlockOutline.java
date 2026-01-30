package graphics;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class BlockOutline {

    public static final float EPS = 0.002f;
    private final List<Float> VERTS = new ArrayList<>();

    private final int vao;
    private final int vbo;

    public BlockOutline() {
        for(int e = 0; e < 12; e++) {
            int[] edge = BlockMesh.EDGE_IDX[e];

            VERTS.add(BlockMesh.EDGE_POS[edge[0]][0]);
            VERTS.add(BlockMesh.EDGE_POS[edge[0]][1]);
            VERTS.add(BlockMesh.EDGE_POS[edge[0]][2]);

            VERTS.add(BlockMesh.EDGE_POS[edge[1]][0]);
            VERTS.add(BlockMesh.EDGE_POS[edge[1]][1]);
            VERTS.add(BlockMesh.EDGE_POS[edge[1]][2]);
        }

        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        this.vbo = glGenBuffers();
        FloatBuffer fb = MemoryUtil.memAllocFloat(VERTS.size());
        for(float f : VERTS)
            fb.put(f);
        fb.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        MemoryUtil.memFree(fb);
    }

    public void render() {
        glBindVertexArray(vao);

        glDrawArrays(GL_LINES, 0, 24);

        glBindVertexArray(0);
    }
}
