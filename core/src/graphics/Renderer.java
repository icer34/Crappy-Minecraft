package graphics;

import core.Window;
import game.Player;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;
import utils.RayCastResult;
import utils.RayCaster;
import world.World;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {

    private final Window window;

    private Shader blockShader;
    private Shader waterShader;
    private Shader lineShader;
    private BlockOutline outline = new BlockOutline();

    private Matrix4f projMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f worldMatrix = new Matrix4f();

    private Frustum frustum = new Frustum();

    private float zNear, zFar;
    private float fov;

    public Renderer(Window window, float fov, float zNear, float zFar) {
        this.window = window;
        this.fov = fov;
        this.zNear = zNear;
        this.zFar = zFar;

        viewMatrix.identity();
        worldMatrix.identity();

        this.blockShader = new Shader();
        blockShader.createShader("shaders/blockVert.glsl", GL_VERTEX_SHADER);
        blockShader.createShader("shaders/blockFrag.glsl", GL_FRAGMENT_SHADER);
        blockShader.link();

        blockShader.createUniform("projMatrix");
        blockShader.createUniform("viewMatrix");
        blockShader.createUniform("worldMatrix");

        blockShader.createUniform("lightDir");
        blockShader.createUniform("lightColor");
        blockShader.createUniform("ambientStrength");
        blockShader.createUniform("fogColor");
        blockShader.createUniform("camPos");
        blockShader.createUniform("isUnderWater");
        blockShader.createUniform("fogDensity");

        blockShader.createUniform("texture_sampler");

        this.waterShader = new Shader();
        waterShader.createShader("shaders/waterVert.glsl", GL_VERTEX_SHADER);
        waterShader.createShader("shaders/waterFrag.glsl", GL_FRAGMENT_SHADER);
        waterShader.link();

        waterShader.createUniform("projMatrix");
        waterShader.createUniform("viewMatrix");
        waterShader.createUniform("worldMatrix");
        waterShader.createUniform("time");

        waterShader.createUniform("lightDir");
        waterShader.createUniform("lightColor");
        waterShader.createUniform("ambientStrength");
        waterShader.createUniform("fogColor");
        waterShader.createUniform("camPos");
        waterShader.createUniform("isUnderWater");
        waterShader.createUniform("fogDensity");
        waterShader.createUniform("waterTransparency");

        waterShader.createUniform("texture_sampler");

        this.lineShader = new Shader();
        lineShader.createShader("shaders/lineVert.glsl", GL_VERTEX_SHADER);
        lineShader.createShader("shaders/lineFrag.glsl", GL_FRAGMENT_SHADER);
        lineShader.createShader("shaders/lineGeom.glsl", GL_GEOMETRY_SHADER);
        lineShader.link();

        lineShader.createUniform("projMatrix");
        lineShader.createUniform("viewMatrix");
        lineShader.createUniform("worldMatrix");
        lineShader.createUniform("viewport");
        lineShader.createUniform("thickness");
    }

    public void render(World world, Player player) {
        Camera cam = player.getCam();

        viewMatrix = cam.getViewMatrix(viewMatrix);
        projMatrix.identity().perspective(Math.toRadians(fov), (float) window.getWidth() / window.getHeight(), zNear, zFar);

        frustum.update(projMatrix, viewMatrix);

        blockShader.bind();
        blockShader.setUniform("projMatrix", projMatrix);
        blockShader.setUniform("viewMatrix", viewMatrix);
        blockShader.setUniform("camPos", cam.getPosition());
        blockShader.unbind();

        waterShader.bind();
        waterShader.setUniform("projMatrix", projMatrix);
        waterShader.setUniform("viewMatrix", viewMatrix);
        waterShader.setUniform("camPos", cam.getPosition());
        waterShader.unbind();

        world.render(blockShader, waterShader, frustum);

        //targeted block outline
        RayCaster caster = new RayCaster(world);
        RayCastResult result = caster.castRay(player.getEyePos(), cam.getFront(), player.getReach());

        if(result.hit()) {
            lineShader.bind();

            lineShader.setUniform("projMatrix", projMatrix);
            lineShader.setUniform("viewMatrix", viewMatrix);
            Matrix4f worldMatrix = new Matrix4f();
            Vector3i wPos = result.targetPos();
            worldMatrix.identity()
                    .translate(wPos.x + 0.5f, wPos.y + 0.5f, wPos.z + 0.5f)
                    .scale(1.0f + BlockOutline.EPS)
                    .translate(-0.5f, -0.5f, -0.5f);
            lineShader.setUniform("worldMatrix", worldMatrix);

            lineShader.setUniform("viewport", new Vector2f(window.getWidth(), window.getHeight()));
            lineShader.setUniform("thickness", 1.5f);
            outline.render();

            lineShader.unbind();
        }
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getFov() {
        return fov;
    }
}
