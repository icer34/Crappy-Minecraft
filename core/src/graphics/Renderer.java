package graphics;

import blocks.Block;
import blocks.MultiTexturedBlock;
import core.Window;
import game.Player;
import org.joml.Math;
import org.joml.*;
import utils.Faces;
import utils.ModelParser;
import utils.RayCastResult;
import utils.RayCaster;
import world.BlockRegistry;
import world.Chunk;
import world.World;

import java.util.Collection;

import static org.joml.Math.abs;
import static org.joml.Math.floor;
import static org.lwjgl.opengl.GL33.*;

public class Renderer {

    private final Window window;
    private final BlockRegistry blockRegistry;

    private final TextureAtlas textureAtlas;
    private final TintTexture tintTexture;

    private ShaderProgram blockShaderProgram;
    private ShaderProgram waterShaderProgram;
    private ShaderProgram lineShaderProgram;
    private ShaderProgram playerShaderProgram;
    private BlockOutline outline = new BlockOutline();

    private StandardMesh playerModelMesh = new StandardMesh();

    private Matrix4f projMatrix = new Matrix4f().identity();
    private Matrix4f viewMatrix = new Matrix4f().identity();
    private Matrix4f worldMatrix = new Matrix4f().identity();

    private Frustum frustum = new Frustum();

    private float zNear, zFar;
    private float fov;

    private int renderedChunks = 0;

    private Vector3f waterFogColor = new Vector3f(0.003f, 0.1f, 0.28f);
    private float waterFogDensity = 0.12f;
    private float waterTransparency = 0.6f;

    public Renderer(Window window, BlockRegistry registry, float fov, float zNear, float zFar) {
        this.window = window;
        this.blockRegistry = registry;
        this.fov = fov;
        this.zNear = zNear;
        this.zFar = zFar;
        this.textureAtlas = new TextureAtlas(16);
        this.playerModelMesh.update(ModelParser.parseOBJ("models/playerModel.obj"));

        //set up the texture atlas
        for(Block b : blockRegistry.getBlocks()) {
            for(int f : Faces.ALL) {
                if(b instanceof MultiTexturedBlock mtb) {
                    int ovrTextureID =  textureAtlas.insert(mtb.ovrTextureKey(f));
                    mtb.setOvrTextureID(f, ovrTextureID);
                }
                int baseTextureID = textureAtlas.insert(b.textureKey(f));
                b.setTextureID(f, baseTextureID);
            }
        }
        textureAtlas.generateMipmaps();

        //create tint texture according to biome data
        this.tintTexture = new TintTexture();

        this.blockShaderProgram = createShaderProgram("block shader",
                                                      "shaders/blockVert.glsl",
                                                      "shaders/blockFrag.glsl");

        blockShaderProgram.createUniforms(new String[] {
                "projMatrix",
                "viewMatrix",
                "worldMatrix",
                "fogColor",
                "camPos",
                "isUnderWater",
                "fogDensity",
                "textureAtlas",
                "biomeMapTex",
                "tintTexture",
                "texture_padding",
                "texture_size",
                "atlas_size",
                "slots_per_row"
        });

        blockShaderProgram.setUniforms(
                new String[]{"texture_padding", "texture_size", "atlas_size", "slots_per_row", "textureAtlas", "biomeMapTex", "tintTexture", "fogColor", "fogDensity"},
                new Object[]{16.0f, 16.0f, 1024.0f, (float)(1024 / (16 + 16)), 0, 1, 2, waterFogColor, waterFogDensity}
                );


        this.waterShaderProgram = createShaderProgram("water shader",
                                                      "shaders/waterVert.glsl",
                                                      "shaders/waterFrag.glsl");

        waterShaderProgram.createUniforms(new String[]{
                "projMatrix",
                "viewMatrix",
                "worldMatrix",
                "fogColor",
                "camPos",
                "isUnderWater",
                "fogDensity",
                "waterTransparency",
                "textureAtlas",
                "biomeMapTex",
                "tintTexture",
                "texture_padding",
                "texture_size",
                "atlas_size",
                "slots_per_row"
        });

        waterShaderProgram.setUniforms(
                new String[]{"texture_padding", "texture_size", "atlas_size", "slots_per_row", "waterTransparency", "textureAtlas", "biomeMapTex", "tintTexture", "fogColor", "fogDensity"},
                new Object[]{16.0f, 16.0f, 1024.0f, (float)(1024 / (16 + 16)), waterTransparency, 0, 1, 2, waterFogColor, waterFogDensity}
                );


        this.lineShaderProgram = createShaderProgram("line shader",
                                                     "shaders/lineVert.glsl",
                                                     "shaders/lineFrag.glsl",
                                                     "shaders/lineGeom.glsl");

        lineShaderProgram.createUniforms(new String[]{
                "projMatrix",
                "viewMatrix",
                "worldMatrix",
                "viewport",
                "thickness",
        });

        lineShaderProgram.setUniforms(new String[]{"thickness"},
                                       new Object[]{1.5f});


        this.playerShaderProgram = createShaderProgram("player shader",
                                                       "shaders/playerVert.glsl",
                                                       "shaders/playerFrag.glsl");

        playerShaderProgram.createUniforms(new String[]{
                "projMatrix",
                "viewMatrix",
                "worldMatrix",
        });
    }

    public void render(World world, Player player) {
        Camera cam = player.getCam();
        Collection<Chunk> chunks = world.getChunks();

        renderedChunks = 0;

        viewMatrix = cam.getViewMatrix(viewMatrix);
        projMatrix.identity().perspective(Math.toRadians(fov), (float) window.getWidth() / window.getHeight(), zNear, zFar);

        frustum.update(projMatrix, viewMatrix);
        frustum.cull(chunks);

        blockShaderProgram.setUniforms(
                new String[]{"projMatrix", "viewMatrix", "camPos", "isUnderWater"},
                new Object[]{projMatrix, viewMatrix, cam.getPosition(), world.isPlayerUnderWater() ? 1 : 0});

        waterShaderProgram.setUniforms(
                new String[]{"projMatrix", "viewMatrix", "camPos", "isUnderWater"},
                new Object[]{projMatrix, viewMatrix, cam.getPosition(), world.isPlayerUnderWater() ? 1 : 0});

        lineShaderProgram.setUniforms(
                new String[]{"projMatrix", "viewMatrix"},
                new Object[]{projMatrix, viewMatrix}
                );

        playerShaderProgram.setUniforms(
                new String[]{"projMatrix", "viewMatrix", "worldMatrix"},
                new Object[]{projMatrix, viewMatrix, worldMatrix.identity().translate(player.getPos())}
                );

        blockShaderProgram.bind();

        glActiveTexture(GL_TEXTURE0);
        textureAtlas.bind();

        glActiveTexture(GL_TEXTURE2);
        tintTexture.bind();

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        for(Chunk c : chunks) {
            //don't render if out of the frustum
            if(!c.isVisible()) continue;

            //don't render if out of the render distance (not the same as the load distance)
            //set not visible so we don't test again in the water pass
            int pcx = (int) floor(player.getPos().x / world.getChunkSize());
            int pcz = (int) floor(player.getPos().z / world.getChunkSize());
            if ( abs(c.getChunkX() - pcx) > world.getRenderDistance()) {
                c.setVisible(false);
                continue;
            }
            if ( abs(c.getChunkZ() - pcz) > world.getRenderDistance()) {
                c.setVisible(false);
                continue;
            }

            glActiveTexture(GL_TEXTURE1);
            c.bindBiomeMapTexture();
            c.updateBiomeMapTexture();

            worldMatrix.identity().translate(c.getWorldPos());

            blockShaderProgram.setUniform("worldMatrix", worldMatrix);
            c.renderSolid();

            renderedChunks++;
        }
        blockShaderProgram.unbind();

        waterShaderProgram.bind();
        glDisable(GL_CULL_FACE);

        glActiveTexture(GL_TEXTURE0);
        textureAtlas.bind();

        glActiveTexture(GL_TEXTURE2);
        tintTexture.bind();
        for(Chunk c : chunks) {
            //don't render if out of the frustum
            if(!c.isVisible()) continue;

            glActiveTexture(GL_TEXTURE1);
            c.bindBiomeMapTexture();
            c.updateBiomeMapTexture();

            worldMatrix.identity().translate(c.getWorldPos());

            waterShaderProgram.setUniform("worldMatrix", worldMatrix);

            c.renderWater();
        }
        waterShaderProgram.unbind();

        drawBlockOutline(world, player);

        //DO THIS ONLY IF THE PLAYER IS IN 3rd PERSON CAM
        //playerShaderProgram.bind();
        //playerModelMesh.draw();
    }

    private ShaderProgram createShaderProgram(String name, String vertPath, String fragPath, String ... extraPaths) {
        ShaderProgram program = new ShaderProgram(name);
        program.addShader(vertPath, GL_VERTEX_SHADER);

        for(String path : extraPaths) {
            program.addShader(path, GL_GEOMETRY_SHADER);
        }

        program.addShader(fragPath, GL_FRAGMENT_SHADER);

        program.link();

        return program;
    }

    private void drawBlockOutline(World world, Player player) {
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
        Vector2f viewport = new Vector2f(window.getWidth(), window.getHeight());

        lineShaderProgram.bind();
        lineShaderProgram.setUniforms(new String[]{"worldMatrix", "viewport"},
                                      new Object[]{worldMatrix, viewport});

        outline.render();

        lineShaderProgram.unbind();
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

    public int getRenderedChunks() {
        return renderedChunks;
    }

    public float getWaterFogDensity() {
        return waterFogDensity;
    }

    public void setWaterFogDensity(float val) {
        waterFogDensity = val;
    }

    public float getWaterTransparency() {
        return waterTransparency;
    }

    public void setWaterTransparency(float val) {
        waterTransparency = val;
    }
}
