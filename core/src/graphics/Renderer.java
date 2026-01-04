package graphics;

import core.Window;
import org.joml.Math;
import org.joml.*;
import world.World;

import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private final Window window;

    private Shader blockShader;
    private Shader waterShader;

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
    }

    public void render(World world, Camera cam) {
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
