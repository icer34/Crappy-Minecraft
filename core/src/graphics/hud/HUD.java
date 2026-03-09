package graphics.hud;

import graphics.ShaderProgram;
import graphics.mesh.HUDMesh;
import graphics.mesh.MeshData;
import org.joml.Matrix4f;
import utils.Window;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL33.*;

public class HUD {

    ShaderProgram hudShader;
    ArrayList<HUDElement> elements = new ArrayList<>();

    private float scale = 3.5f;

    public HUD(Window window) {
        //----SETUP SHADER----
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

        //----ADD INDIVIDUAL ELEMENTS----
        elements.add(new Crosshair(window.getWidth(), window.getHeight(), scale,
                                   "textures/gui/sprites/hud/crosshair.png"));
        elements.add(new Hotbar(window.getWidth(), window.getHeight(), scale,
                                "textures/gui/sprites/hud/hotbar.png"));
    }

    public void draw() {
        hudShader.bind();
        glDisable(GL_DEPTH_TEST);

        for(HUDElement e : elements) {
            e.draw();
        }

        glEnable(GL_DEPTH_TEST);
        hudShader.unbind();
    }

    public void setScale(float value) {
        this.scale = value;
        for(HUDElement e : elements) {
            e.setScale(scale);
        }
    }

    public float getScale() {
        return scale;
    }
}
