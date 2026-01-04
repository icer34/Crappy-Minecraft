package graphics;

import core.*;

import game.*;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import org.joml.Vector2i;
import org.joml.Vector3f;
import utils.Direction;
import utils.Utils;
import world.World;

public class GUI {

    Window window;
    Renderer renderer;
    Camera cam;
    Player player;
    PlayerManager playerManager;
    World world;

    ImGuiImplGlfw imGuiGlfw;
    ImGuiImplGl3 imGuiGl3;

    private boolean showSettings = false;
    private boolean showDebug = true;

    //general settings
    private float fov;
    private int fps;
    private float sens;
    private float flySpeed;
    private int renderDistance;
    private float timeSpeed;
    private int gameMode;

    //terrain gen settings
    private int octaves;
    private float lacunarity;
    private float gain;
    private float scale;
    private int seed;
    private int seaLvl;

    //shader settings
    private float waterFogDensity;
    private float waterTransparency;


    public GUI(Window window, Renderer renderer, Player player, PlayerManager playerManager, World world) {
        this.window = window;
        this.renderer = renderer;
        this.cam = player.getCam();
        this.player = player;
        this.world = world;
        this.playerManager = playerManager;

        this.imGuiGlfw = new ImGuiImplGlfw();
        this.imGuiGl3 = new ImGuiImplGl3();

        this.waterFogDensity = world.getWaterFogDensity();
        this.waterTransparency = world.getWaterTransparency();

        this.sens = cam.getSens();
        this.flySpeed = player.getFlySpeed();
        this.fov = renderer.getFov();
        this.fps = 0;
        this.renderDistance = world.getRenderDistance();
        this.timeSpeed = world.getTimeSpeed();
        this.gameMode = (playerManager.getGameMode() instanceof SurvivalMode) ? 0 : 1;

        this.octaves = Utils.TERRAIN_GENERATOR.getOctaves();
        this.lacunarity = Utils.TERRAIN_GENERATOR.getLacunarity();
        this.gain = Utils.TERRAIN_GENERATOR.getGain();
        this.scale = Utils.TERRAIN_GENERATOR.getScale();
        this.seed = Utils.TERRAIN_GENERATOR.getSeed();
        this.seaLvl = Utils.TERRAIN_GENERATOR.getSeaLvl();

        ImGui.createContext();

        ImGui.getIO().getFonts().build();

        ImGui.styleColorsDark();

        imGuiGlfw.init(window.getWindowHandle(), true);
        imGuiGl3.init("#version 330");
    }

    public void newFrame() {
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void render() {

        if(showDebug)
            renderDebug();

        if(showSettings)
            renderSettings();
    }

    private void renderDebug() {
        ImGui.begin("Debug panel");

        ImGui.text("FPS: " + fps);

        ImGui.text("Loaded chunks: " + world.getLoadedChunks());

        ImGui.text("Rendered chunks: " + world.getRenderedChunks());

        Vector3f pos = player.getPos();
        String s = String.format("Player coords x: %.3f y: %.3f z: %.3f", pos.x, pos.y, pos.z);
        ImGui.text(s);

        Vector2i chunkCoords = world.getChunkCoords(pos);
        String s4 = String.format("Chunk coords x: %d z: %d", chunkCoords.x, chunkCoords.y);
        ImGui.text(s4);

        Vector3f vel = player.getVel();
        String s3 = String.format("Player vel x: %.3f y: %.3f z: %.3f", vel.x, vel.y, vel.z);
        ImGui.text(s3);

        int groundHeight = world.getGroundHeight((int)pos.x, (int)pos.z);
        ImGui.text("Ground height: " + groundHeight);

        Direction dir = cam.getCardinalDirection();
        ImGui.text("Facing: " + dir.toString());

        boolean freeCam = player.isFreeCam();
        ImGui.text("Free cam toggled: " + freeCam);

        ImGui.end();
    }

    private void renderGeneralSettings() {
        int[] renderDistanceArray = {renderDistance};
        if (ImGui.sliderInt("Render Distance", renderDistanceArray, 2, 32)) {
            renderDistance = renderDistanceArray[0];
            world.setRenderDistance(renderDistance);
        }

        float[] fovArray = {fov};
        if (ImGui.sliderFloat("FOV", fovArray, 30.0f, 120.0f)) {
            fov = fovArray[0];
            renderer.setFov(fov);
        }

        float[] sensArray = {sens};
        if (ImGui.sliderFloat("Sens", sensArray, 0.01f, 1.0f)) {
            sens = sensArray[0];
            cam.setSens(sens);
        }

        float[] flySpeedArray = {flySpeed};
        if (ImGui.sliderFloat("Fly speed", flySpeedArray, 5.0f, 50.0f)) {
            flySpeed = flySpeedArray[0];
            player.setFlySpeed(flySpeed);
        }

        float[] timeSpeedArray = {timeSpeed};
        if (ImGui.sliderFloat("Time speed", timeSpeedArray, 0.1f, 10.0f)) {
            timeSpeed = timeSpeedArray[0];
            world.setTimeSpeed(timeSpeed);
        }

        int currentGameMode = gameMode;
        String[] gameModes = {"Survival", "Spectator"};
        if (ImGui.beginCombo("Game mode", gameModes[currentGameMode])) {
            for (int i = 0; i < gameModes.length; i++) {
                boolean selected = (currentGameMode == i);
                if (ImGui.selectable(gameModes[i], selected)) {
                    gameMode = i;
                    if(gameMode == 0) {
                        playerManager.setMode(player, new SurvivalMode(world));
                    } else {
                        playerManager.setMode(player, new SpectatorMode(world));
                    }
                }
            }
            ImGui.endCombo();
        }
    }

    private void renderShaderSettings() {
        float[] waterFogDensityArray = {waterFogDensity};
        if (ImGui.sliderFloat("Water fog density", waterFogDensityArray, 0.01f, 0.5f)) {
            waterFogDensity = waterFogDensityArray[0];
            world.setWaterFogDensity(waterFogDensity);
        }

        float[] waterTransparencyArray = {waterTransparency};
        if (ImGui.sliderFloat("Water transparency", waterTransparencyArray, 0.0f, 1.0f)) {
            waterTransparency = waterTransparencyArray[0];
            world.setWaterTransparency(waterTransparency);
        }
    }

    private void renderTerrainSettings() {
        int[] octaveArray = {octaves};
        if (ImGui.sliderInt("Octaves", octaveArray, 1, 10)) {
            octaves = octaveArray[0];
            Utils.TERRAIN_GENERATOR.setOctaves(octaves);
        }

        float[] lacunarityArray = {lacunarity};
        if (ImGui.sliderFloat("Lacunarity", lacunarityArray, 1.8f, 3.0f)) {
            lacunarity = lacunarityArray[0];
            Utils.TERRAIN_GENERATOR.setLacunarity(lacunarity);
        }

        float[] gainArray = {gain};
        if (ImGui.sliderFloat("Gain", gainArray, 0.3f, 0.7f)) {
            gain = gainArray[0];
            Utils.TERRAIN_GENERATOR.setGain(gain);
        }

        float[] scaleArray = {scale};
        if (ImGui.sliderFloat("Scale", scaleArray, 0.001f, 0.2f)) {
            scale = scaleArray[0];
            Utils.TERRAIN_GENERATOR.setScale(scale);
        }

        ImInt seaLvlValue = new ImInt(seaLvl);
        if (ImGui.inputInt("Sea level", seaLvlValue)) {
            seaLvl = seaLvlValue.get();
            world.setSeaLvl(seaLvl);
        }

        ImInt seedValue = new ImInt(seed);
        if (ImGui.inputInt("Seed", seedValue)) {
            seed = seedValue.get();
            Utils.TERRAIN_GENERATOR.setSeed(seed);
        }

        if (ImGui.button("Regenerate terrain")) {
            world.regenerate();
        }
    }

    private void renderSettings() {
        ImGui.begin("Settings");

        if (ImGui.beginTabBar("settings_tabs")) {

            if (ImGui.beginTabItem("General")) {
                renderGeneralSettings();
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Terrain")) {
                renderTerrainSettings();
                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Shader")) {
                renderShaderSettings();
                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }

        ImGui.end();
    }

    public boolean showDebug() {
        return showDebug;
    }

    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }

    public void setShowSettings(boolean value) {
        this.showSettings = value;
    }

    public boolean showSettings() {
        return showSettings;
    }

    public void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void cleanup() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    public void updateFps(int fps) {
        this.fps = fps;
    }
}
