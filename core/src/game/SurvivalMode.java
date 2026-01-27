package game;

import blocks.BlockType;
import org.joml.Vector3f;
import org.joml.Vector3i;
import utils.Input;
import utils.RayCastResult;
import utils.RayCaster;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.joml.Math.*;

public class SurvivalMode implements GameMode{

    private World world;
    private RayCaster rayCaster;

    public SurvivalMode(World world) {
        this.world = world;
        this.rayCaster = new RayCaster(world);
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

        RayCastResult rayCastResult = rayCaster.castRay(player.getCam().getPosition(), player.getCam().getFront(), player.getReach());
        processPlayerActions(player, input, rayCastResult);

        Vector3f delta = processPlayerMovement(player, input, dt);

        Vector3f dx = new Vector3f(delta.x, 0.0f, 0.0f);
        if(!isCollision(player, world, dx)) player.applyHorizontalDelta(dx);

        Vector3f dz = new Vector3f(0.0f, 0.0f, delta.z);
        if(!isCollision(player, world, dz)) player.applyHorizontalDelta(dz);

        applyGravity(player, world, dt);
    }

    private void processPlayerActions(Player player, Input input, RayCastResult rayCastResult) {
        // --- PLAYER BROKE BLOCK ---
        if(input.consumeButtonPress(GLFW_MOUSE_BUTTON_1) && rayCastResult.hit()) {
            world.breakBlock(rayCastResult.targetPos());
        }

        // --- PLAYER PLACED BLOCK ---
        if(input.consumeButtonPress(GLFW_MOUSE_BUTTON_2) && rayCastResult.hit()) {

            Vector3i targetPos = rayCastResult.targetPos();
            targetPos.add(rayCastResult.targetNorm());

            Vector3f playerPos = player.getPos();
            Vector3i playerBlockPos = new Vector3i((int) floor(playerPos.x), (int) floor(playerPos.y), (int) floor(playerPos.z));
            if(targetPos.equals(playerBlockPos) || targetPos.equals(playerBlockPos.add(0, 1, 0))) return;

            world.setBlock(player.getSelectedBlock(), targetPos);
        }
    }

    private Vector3f processPlayerMovement(Player player, Input input, float dt) {
        // --- Rotation ---
        float dx = (float) input.consumeDx();
        float dy = (float) input.consumeDy();
        player.rotate(dx, dy);

        // --- Movement ---
        float inputX = 0f;
        float inputZ = 0f;

        if (input.isKeyPressed(GLFW_KEY_SPACE) && player.isOnGround()) player.jump();

        if (input.isKeyPressed(GLFW_KEY_A)) inputX -= 1f;
        if (input.isKeyPressed(GLFW_KEY_D)) inputX += 1f;
        if (input.isKeyPressed(GLFW_KEY_W)) inputZ += 1f;
        if (input.isKeyPressed(GLFW_KEY_S)) inputZ -= 1f;

        return player.computeWalkDelta(inputX, inputZ, dt, new Vector3f());
    }

    private boolean isCollision(Player player, World world, Vector3f delta) {
        Vector3f p = player.getPos();
        float[] hb = player.getHitBox();

        float r = hb[0] * 0.5f;
        float h = hb[1];

        // --- X ---
        if (delta.x != 0.0f) {
            float testX = p.x + delta.x + (delta.x > 0 ? r : -r);

            if (world.getBlockAt(new Vector3f(testX, p.y + 0.2f, p.z)).solid ||
                    world.getBlockAt(new Vector3f(testX, p.y + 1.2f, p.z)).solid) {
                return true;
            }
        }

        // --- Z ---
        if (delta.z != 0.0f) {
            float testZ = p.z + delta.z + (delta.z > 0 ? r : -r);

            if (world.getBlockAt(new Vector3f(p.x, p.y + 0.2f, testZ)).solid ||
                    world.getBlockAt(new Vector3f(p.x, p.y + 1.2f, testZ)).solid) {
                return true;
            }
        }

        // --- Y (sol/plafond) ---
        final float EPS = 0.01f;

        if (delta.y < 0.0f) {
            float testY = p.y + delta.y - EPS;

            if (world.getBlockAt(new Vector3f(p.x - r, testY, p.z - r)).solid ||
                    world.getBlockAt(new Vector3f(p.x + r, testY, p.z - r)).solid ||
                    world.getBlockAt(new Vector3f(p.x - r, testY, p.z + r)).solid ||
                    world.getBlockAt(new Vector3f(p.x + r, testY, p.z + r)).solid) {
                return true;
            }
        } else if (delta.y > 0.0f) {

            float testY = p.y + h + delta.y + EPS;

            if (world.getBlockAt(new Vector3f(p.x - r, testY, p.z - r)).solid ||
                    world.getBlockAt(new Vector3f(p.x + r, testY, p.z - r)).solid ||
                    world.getBlockAt(new Vector3f(p.x - r, testY, p.z + r)).solid ||
                    world.getBlockAt(new Vector3f(p.x + r, testY, p.z + r)).solid) {
                return true;
            }
        }

        return false;
    }

    private void applyGravity(Player player, World world, float dt) {
        final float EPS = 0.01f;

        Vector3f p = player.getPos();
        float[] hb = player.getHitBox();
        float h = hb[1];

        if(player.getVel().y > 0) {
            player.setOnGround(false);
        } else {
            float supportYNow = highestSupportY(player, world, p.y - EPS);
            if (supportYNow != Float.NEGATIVE_INFINITY) {

                player.setOnGround(true);
                player.setYVel(0.0f);

                player.setYPos(supportYNow);
            } else {
                player.setOnGround(false);
            }
        }

        float dy = player.computeDy(dt);

        if (!player.isOnGround()) {
            if (dy < 0.0f) {
                float supportYFall = highestSupportY(player, world, p.y + dy - EPS);

                if (supportYFall != Float.NEGATIVE_INFINITY) {
                    player.setYVel(0.0f);
                    player.setYPos(supportYFall);
                    player.setOnGround(true);
                } else {
                    player.moveWorld(new Vector3f(0.0f, dy, 0.0f));
                }
            } else if (dy > 0.0f) {
                float ceilingBottom = lowestCeilingBottomY(player, world, p.y + h + dy + EPS);

                if (ceilingBottom != Float.POSITIVE_INFINITY) {
                    player.setYVel(0.0f);
                    player.setYPos(ceilingBottom - h - EPS);
                } else {
                    player.moveWorld(new Vector3f(0.0f, dy, 0.0f));
                }
            }
        }
    }

    private float highestSupportY(Player player, World world, float probeY) {
        Vector3f p = player.getPos();
        float[] hb = player.getHitBox();

        float r = hb[0] * 0.5f;

        float best = Float.NEGATIVE_INFINITY;

        float[] xs = new float[]{p.x - r, p.x + r};
        float[] zs = new float[]{p.z - r, p.z + r};

        for (float x : xs) {
            for (float z : zs) {
                if (world.getBlockAt(new Vector3f(x, probeY, z)).solid) {
                    // bloc touché = floor(probeY), top = +1
                    float topY = (float) Math.floor(probeY) + 1.0f;
                    if (topY > best) best = topY;
                }
            }
        }
        return best;
    }

    private float lowestCeilingBottomY(Player player, World world, float probeY) {
        Vector3f p = player.getPos();
        float[] hb = player.getHitBox();

        float r = hb[0] * 0.5f;

        float best = Float.POSITIVE_INFINITY;

        float[] xs = new float[]{p.x - r, p.x + r};
        float[] zs = new float[]{p.z - r, p.z + r};

        for (float x : xs) {
            for (float z : zs) {
                if (world.getBlockAt(new Vector3f(x, probeY, z)).solid) {
                    // bloc touché = floor(probeY), bottom = blocY
                    float bottomY = (float) Math.floor(probeY);
                    if (bottomY < best) best = bottomY;
                }
            }
        }
        return best;
    }
}
