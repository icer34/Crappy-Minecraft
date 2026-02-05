package game;

import blocks.Block;
import blocks.BreakableBlock;
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

        Vector3f dx = new Vector3f(delta.x + player.getPos().x, player.getPos().y, player.getPos().z);
        if(!isHorizontalCollision(player, world, dx)) player.applyHorizontalDelta(new Vector3f(delta.x, 0.0f, 0.0f));

        Vector3f dz = new Vector3f(player.getPos().x, player.getPos().y, player.getPos().z + delta.z);
        if(!isHorizontalCollision(player, world, dz)) player.applyHorizontalDelta(new Vector3f(0.0f, 0.0f, delta.z));

        applyGravity(player, world, dt);
    }

    private void processPlayerActions(Player player, Input input, RayCastResult rayCastResult) {
        // --- PLAYER BROKE BLOCK ---
        if(input.consumeButtonPress(GLFW_MOUSE_BUTTON_1) && rayCastResult.hit()) {
            Block b = world.getBlockAt(rayCastResult.targetPos());
            if(b instanceof BreakableBlock bb)
                bb.onBreak(world, rayCastResult.targetPos());
        }

        // --- PLAYER PLACED BLOCK ---
        if(input.consumeButtonPress(GLFW_MOUSE_BUTTON_2) && rayCastResult.hit()) {
            Vector3i targetPos = rayCastResult.targetPos();
            targetPos.add(rayCastResult.targetNorm());

            if(!isPlacementValid(player, targetPos)) return;

            Block b = world.getBlock(player.getSelectedBlock());
            b.onPlacement(world, targetPos);
        }

        // --- PLAYER SELECTED BLOCK ---
        if(input.consumeButtonPress(GLFW_MOUSE_BUTTON_3) && rayCastResult.hit()) {
            Block b = world.getBlockAt(rayCastResult.targetPos());
            player.setSelectedBlock(b.name());
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

    private boolean isPlacementValid(Player player, Vector3i targetPos) {
        Vector3f p = player.getPos();
        float[] hb = player.getHitBox();

        float r = hb[0] * 0.5f;
        float h = hb[1];

        float pMinX = p.x - r;
        float pMaxX = p.x + r;
        float pMinY = p.y;
        float pMaxY = p.y + h;
        float pMinZ = p.z - r;
        float pMaxZ = p.z + r;

        float bMinX = targetPos.x;
        float bMaxX = targetPos.x + 1.0f;
        float bMinY = targetPos.y;
        float bMaxY = targetPos.y + 1.0f;
        float bMinZ = targetPos.z;
        float bMaxZ = targetPos.z + 1.0f;

        boolean overlapX = pMinX < bMaxX && pMaxX > bMinX;
        boolean overlapY = pMinY < bMaxY && pMaxY > bMinY;
        boolean overlapZ = pMinZ < bMaxZ && pMaxZ > bMinZ;

        return !(overlapX && overlapY && overlapZ);
    }

    private boolean isHorizontalCollision(Player player, World world, Vector3f pos) {

        float[] hb = player.getHitBox();
        float r = hb[0] * 0.5f;

        float yLow  = pos.y + 0.2f;
        float yHigh = pos.y + 1.2f;

        // --- X ---
        if (world.getBlockAt(new Vector3f(pos.x - r, yLow,  pos.z)).isSolid() ||
            world.getBlockAt(new Vector3f(pos.x - r, yHigh, pos.z)).isSolid() ||
            world.getBlockAt(new Vector3f(pos.x + r, yLow,  pos.z)).isSolid() ||
            world.getBlockAt(new Vector3f(pos.x + r, yHigh, pos.z)).isSolid())
            return true;

        // --- Z ---
        if (world.getBlockAt(new Vector3f(pos.x, yLow,  pos.z - r)).isSolid() ||
            world.getBlockAt(new Vector3f(pos.x, yHigh, pos.z - r)).isSolid() ||
            world.getBlockAt(new Vector3f(pos.x, yLow,  pos.z + r)).isSolid() ||
            world.getBlockAt(new Vector3f(pos.x, yHigh, pos.z + r)).isSolid())
            return true;

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
                if (world.getBlockAt(new Vector3f(x, probeY, z)).isSolid()) {
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
                if (world.getBlockAt(new Vector3f(x, probeY, z)).isSolid()) {
                    // bloc touché = floor(probeY), bottom = blocY
                    float bottomY = (float) Math.floor(probeY);
                    if (bottomY < best) best = bottomY;
                }
            }
        }
        return best;
    }
}
