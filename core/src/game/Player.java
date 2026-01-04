package game;

import blocks.BlockType;
import graphics.Camera;
import org.joml.Vector3f;

public class Player {

    private Vector3f pos;
    private Vector3f vel;

    private boolean onGround;
    private float[] hitBox;

    private float eyeHeight;
    private Camera cam;
    private boolean freeCam;

    private float reach;
    private BlockType selectedBlock;

    private float walkSpeed;
    private float flySpeed;
    private float jumpVelocity;
    private float gravity;

    public Player(Vector3f initialPos) {
        this.pos = new Vector3f(initialPos);
        this.vel = new Vector3f(0.0f, 0.0f, 0.0f);
        this.hitBox = new float[]{0.5f, 1.9f};
        this.eyeHeight = 1.8f;
        this.walkSpeed = 5.0f;
        this.flySpeed = 10.0f;
        this.cam = new Camera(new Vector3f(pos.x, pos.y + eyeHeight, pos.z));
        this.freeCam = false;
        this.gravity = -15f;
        this.jumpVelocity = 6.0f;
        this.reach = 4.0f;
        this.selectedBlock = BlockType.STONE_BLOCK;
    }

    /*
    * Moves the player in the cam front direction (free cam type of movement)
    */
    public void moveWorld(Vector3f worldDelta) {
        if (freeCam) {
            cam.moveWorld(worldDelta.x, worldDelta.y, worldDelta.z);
            return;
        }
        pos.add(worldDelta);
        syncCam();
    }

    public void rotate(float mouseDx, float mouseDy) {
        cam.rotate(-mouseDy, mouseDx, 0.0f);
    }

    public Vector3f getForwardXZ(Vector3f dest) {
        // ignore pitch pour marcher
        Vector3f f = cam.getFront();
        dest.set(f.x, 0.0f, f.z);
        if (dest.lengthSquared() > 1e-8f) dest.normalize();
        return dest;
    }

    public Vector3f getRightXZ(Vector3f dest) {
        // right horizontal (ignore pitch)
        Vector3f r = cam.getRight();
        dest.set(r.x, 0.0f, r.z);
        if (dest.lengthSquared() > 1e-8f) dest.normalize();
        return dest;
    }

    public Vector3f computeWalkDelta(float inputX, float inputZ, float dt, Vector3f out) {
        Vector3f f = getForwardXZ(new Vector3f());
        Vector3f r = getRightXZ(new Vector3f());

        out.zero();
        out.fma(inputX, r).fma(inputZ, f);

        if (out.lengthSquared() > 1e-8f) out.normalize();

        out.mul(walkSpeed * dt);
        out.y = 0.0f;
        return out;
    }

    public void applyHorizontalDelta(Vector3f deltaXZ) {
        if (freeCam) {
            cam.moveWorld(deltaXZ.x, deltaXZ.y, deltaXZ.z);
            return;
        }
        pos.add(deltaXZ);
        syncCam();
    }

    public float computeDy(float dt) {
        if(freeCam) return 0.0f;
        vel.y += gravity * dt;
        return vel.y * dt;
    }

    public void jump() {
        if (freeCam) return;
        if (!onGround) return;

        vel.y = jumpVelocity;
        onGround = false;
    }

    public void syncCam() {
        cam.setPosition(pos.x, pos.y + eyeHeight, pos.z);
    }

    public Vector3f getEyePos() {
        return cam.getPosition();
    }

    public void setVel(Vector3f vel) {
        this.vel = vel;
    }

    public void setYVel(float value) {
        this.vel.y = value;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
        syncCam();
    }

    public void setYPos(float val) {
        this.pos.y = val;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public void setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public float[] getHitBox() {
        return hitBox;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public Vector3f getVel() {
        return vel;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Camera getCam() {
        return cam;
    }

    public boolean isFreeCam() {
        return freeCam;
    }

    public void setFreeCam(boolean freeCam) {
        this.freeCam = freeCam;
    }

    public float getGravity() {
        return gravity;
    }

    public float getReach() {
        return reach;
    }

    public void setReach(float reach) {
        this.reach = reach;
    }

    public BlockType getSelectedBlock() {
        return selectedBlock;
    }
}
