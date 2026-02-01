package game;

import core.IApplication;
import graphics.Renderer;
import core.Window;
import graphics.*;

import org.joml.Vector3f;
import utils.Input;
import world.World;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;

public class Game implements IApplication {

    Window window;
    Input input;
    Renderer renderer;
    GUI gui;

    PlayerManager playerManager;
    Player player;

    World world;

    private boolean shouldClose = false;

    //fps variables
    private int fpsCounter = 0;
    private long lastTime = System.nanoTime();
    private float deltaTime = 0;
    long fpsStartTime = lastTime;
    private int fps;

    public void run() {

        init();

        while(!shouldClose && !window.shouldClose()) {

            input();

            update();

            render();

            window.update();

        }

        cleanup();
    }

    @Override
    public void init() {
        window = new Window("Crappy Minecraft", 1600, 900, true);
        window.init(input = new Input());

        renderer = new Renderer(window, 80.0f, 0.001f, 1000.0f);

        world = new World(0);

        player = new Player(new Vector3f(0.0f, world.getGroundHeight(0.0f, 0.0f) + 5.0f, 0.0f));
        playerManager = new PlayerManager();
        playerManager.setMode(player, new SurvivalMode(world));

        gui = new GUI(window, renderer, player, playerManager, world);

        //background color
        glClearColor(.0f, .0f, .0f, 1f);
    }

    @Override
    public void input() {
        if(input.consumeKeyPress(GLFW_KEY_ESCAPE)) {
            gui.setShowSettings(!gui.showSettings());
            window.toggleCursor(input);
            if (gui.showSettings()) {
                input.disable();
            } else {
                input.enable();
            }
        }

        if(input.consumeKeyPress(GLFW_KEY_GRAVE_ACCENT)) {
            gui.setShowDebug(!gui.showDebug());
        }
    }

    @Override
    public void update() {
        updateFps();
        playerManager.update(player, world, input, !gui.showSettings(), deltaTime);
        world.update(player.getPos(), deltaTime);
    }

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderer.render(world, player);

        gui.newFrame();

        gui.render();

        gui.endFrame();
    }

    @Override
    public void cleanup() {
        world.cleanup();
        gui.cleanup();
        window.cleanup();
    }

    private void updateFps() {
        fpsCounter++;
        long currentTime = System.nanoTime();
        long frameTime = currentTime - lastTime;
        deltaTime = frameTime / 1_000_000_000.0f;
        lastTime = currentTime;

        long elapsedTime = currentTime - fpsStartTime;
        if (elapsedTime >= 1_000_000_000) {
            fps = fpsCounter;
            fpsCounter = 0;
            fpsStartTime = currentTime;
            gui.updateFps(fps);
        }
    }
}
