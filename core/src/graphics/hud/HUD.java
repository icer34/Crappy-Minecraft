package graphics.hud;

import graphics.ShaderProgram;
import graphics.mesh.MeshData;
import org.joml.Matrix4f;
import utils.Window;

import static org.lwjgl.opengl.GL33.*;

public class HUD {

    ShaderProgram hudShader;

    private HUDTexture crosshairTexture = new HUDTexture("textures/gui/sprites/hud/crosshair.png");
    private HUDMesh crosshairMesh;
    private int crosshairSize = 32;

    public HUD(Window window) {
        hudShader = new ShaderProgram("HUD shader");
        hudShader.addShader("shaders/hudVert.glsl", GL_VERTEX_SHADER);
        hudShader.addShader("shaders/hudFrag.glsl", GL_FRAGMENT_SHADER);
        hudShader.link();

        hudShader.createUniforms(new String[]{
                "projMatrix",
                "hudTexture"
        });

        Matrix4f ortho = new Matrix4f().ortho(0, window.getWidth(),
                                              window.getHeight(), 0,
                                              -1, 1);
        hudShader.setUniforms(new String[]{"projMatrix", "hudTexture"},
                              new Object[]{ortho, 0});

        //create crosshair mesh and set its data
        crosshairMesh = new HUDMesh();
        initCrosshairMesh(window);
    }

    public void draw() {
        glDisable(GL_DEPTH_TEST);
        hudShader.bind();

        crosshairTexture.load();
        crosshairMesh.draw();

        hudShader.unbind();
        glEnable(GL_DEPTH_TEST);
    }

    private void initCrosshairMesh(Window window) {
        //get the center of the window
        float cx = window.getWidth() / 2f;
        float cy = window.getHeight() / 2f;

        float x0 = cx - crosshairSize / 2f;
        float x1 = cx + crosshairSize / 2f;
        float y0 = cy - crosshairSize / 2f;
        float y1 = cy + crosshairSize / 2f;

        float[] vertices = new float[] {x0, y0, 0f, 0f, //top left
                                        x1, y0, 1f, 0f, //top right
                                        x0, y1, 0f, 1f, //bottom left
                                        x1, y1, 1f, 1f};//bottom right

        int[] indices = new int[] {0, 3, 2,
                                   0, 1, 3};

        crosshairMesh.update(new MeshData(vertices, indices));
    }
}
