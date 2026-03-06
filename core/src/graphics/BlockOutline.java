package graphics;

import game.Player;
import graphics.mesh.BlockMesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;
import utils.RayCastResult;
import utils.RayCaster;
import world.World;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class BlockOutline {

    public static final float EPS = 0.002f;
    private final List<Float> VERTS = new ArrayList<>();

    private final int vao;
    private final int vbo;

    ShaderProgram shader;

    public BlockOutline() {
        shader = new ShaderProgram("block outline shader");
        shader.addShader("shaders/lineVert.glsl", GL_VERTEX_SHADER);
        shader.addShader("shaders/lineFrag.glsl", GL_FRAGMENT_SHADER);
        shader.addShader("shaders/lineGeom.glsl", GL_GEOMETRY_SHADER);
        shader.link();

        shader.createUniforms(new String[]{
                "projMatrix",
                "viewMatrix",
                "worldMatrix",
                "viewport",
                "thickness",
        });

        shader.setUniforms(new String[]{"thickness"},
                                      new Object[]{1.5f});

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

    public void draw(World world, Player player, int wWidth, int wHeight, Matrix4f viewMatrix, Matrix4f projMatrix) {

        //targeted block outline
        RayCaster caster = new RayCaster(world);
        RayCastResult result = caster.castRay(player.getEyePos(), player.getCam().getFront(), player.getReach());

        if(!result.hit()) return;

        //compute world matrix
        Matrix4f worldMatrix = new Matrix4f();
        Vector3i wPos = result.targetPos();
        worldMatrix.identity()
                   .translate(wPos.x + 0.5f, wPos.y + 0.5f, wPos.z + 0.5f)
                   .scale(1.0f + BlockOutline.EPS)
                   .translate(-0.5f, -0.5f, -0.5f);
        Vector2f viewport = new Vector2f(wWidth, wHeight);

        shader.bind();
        shader.setUniforms(
                new String[]{"projMatrix", "viewMatrix", "worldMatrix", "viewport"},
                new Object[]{projMatrix, viewMatrix, worldMatrix, viewport}
        );

        glBindVertexArray(vao);
        glDisable(GL_CULL_FACE);

        glDrawArrays(GL_LINES, 0, 24);

        glEnable(GL_CULL_FACE);
        glBindVertexArray(0);

        shader.unbind();
    }
}
