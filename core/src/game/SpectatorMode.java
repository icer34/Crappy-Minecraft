package game;

import graphics.Camera;
import org.joml.Vector3f;
import utils.Input;
import game.world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class SpectatorMode implements GameMode {

    private World world;

    public SpectatorMode(World world) {
        this.world = world;
    }

    @Override
    public void onEnter(Player player) {
        player.setVel(new Vector3f(0.0f, 0.0f, 0.0f));
    }

    @Override
    public void onExit(Player player) {
        player.setVel(new Vector3f(0.0f, 0.0f, 0.0f));
        player.setOnGround(false);
    }

    @Override
    public void update(Player player, Input input, float dt) {
        processInput(player, input, dt);
    }

    private void processInput(Player player, Input input, float dt) {
        Camera cam = player.getCam();

        // --- Rotation ---
        float dx = (float) input.consumeDx();
        float dy = (float) input.consumeDy();
        player.rotate(dx, dy);

        // --- Toggle freeCam ---
        if (input.consumeKeyPress(GLFW_KEY_F)) {
            boolean state = player.isFreeCam();
            if (state) player.syncCam();
            player.setFreeCam(!state);

            if(player.isFreeCam())
                player.moveWorld(new Vector3f(0.1f, 0.1f, 0.1f));
        }

        // --- Build spectator movement in WORLD space ---
        float speed = player.getFlySpeed();
        float step = speed * dt;

        Vector3f forward = new Vector3f(cam.getFront()).normalize();

        Vector3f right = new Vector3f(cam.getRight());

        Vector3f move = new Vector3f();
        if (input.isKeyPressed(GLFW_KEY_W)) move.fma(step, forward);
        if (input.isKeyPressed(GLFW_KEY_S)) move.fma(-step, forward);

        if (input.isKeyPressed(GLFW_KEY_D)) move.fma(step, right);
        if (input.isKeyPressed(GLFW_KEY_A)) move.fma(-step, right);

        if (input.isKeyPressed(GLFW_KEY_SPACE)) move.y += step;
        if (input.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) move.y -= step;

        if (move.lengthSquared() > 0.0f) {
            player.moveWorld(move);
        }
    }
}
