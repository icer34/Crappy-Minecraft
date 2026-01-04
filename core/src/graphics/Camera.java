package graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import utils.Direction;

public class Camera {

    private final Vector3f position = new Vector3f();

    private final Vector3f rotation = new Vector3f(0, 0, 0);

    private float sens = 0.15f;
    private float maxPitch = 89.0f;

    private final Vector3f front = new Vector3f(0, 0, -1);
    private final Vector3f right = new Vector3f(1, 0, 0);
    private final Vector3f up    = new Vector3f(0, 1, 0);

    private boolean dirtyBasis = true;

    public Camera(Vector3f initialPos) {
        this.position.set(initialPos);
    }

    // -------------------------
    // Position / Rotation API
    // -------------------------

    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setPosition(Vector3f p) {
        position.set(p);
    }

    /** Rotation absolue en degrés */
    public void setRotation(float pitchDeg, float yawDeg, float rollDeg) {
        rotation.set(pitchDeg, yawDeg, rollDeg);
        clampRotation();
        dirtyBasis = true;
    }

    /** Rotation delta : dx=pitchDelta, dy=yawDelta, dz=rollDelta (input) */
    public void rotate(float dx, float dy, float dz) {
        rotation.x += dx * sens;  // pitch
        rotation.y += dy * sens;  // yaw
        rotation.z += dz * sens;  // roll (souvent 0)

        clampRotation();
        dirtyBasis = true;
    }

    private void clampRotation() {
        // clamp pitch
        if (rotation.x >  maxPitch) rotation.x =  maxPitch;
        if (rotation.x < -maxPitch) rotation.x = -maxPitch;

        // yaw wrap (optionnel)
        rotation.y = wrapAngle(rotation.y);
        rotation.z = wrapAngle(rotation.z);
    }

    private float wrapAngle(float a) {
        a %= 360.0f;
        if (a > 180.0f) a -= 360.0f;
        if (a < -180.0f) a += 360.0f;
        return a;
    }

    // -------------------------
    // World movement helpers
    // -------------------------

    /** Déplacement MONDE brut (pas de rotation appliquée) */
    public void moveWorld(float x, float y, float z) {
        position.add(x, y, z);
    }

    /** Déplacement LOCAL caméra (strafe/forward selon yaw+pitch). Utile pour freecam/spectator. */
    public void moveLocal(float dx, float dy, float dz) {
        updateBasisIfNeeded();

        // dx = strafe (right), dz = forward, dy = up (world up)
        position.fma(dx, right);
        position.y += dy;
        position.fma(dz, front);
    }

    // -------------------------
    // Direction vectors
    // -------------------------

    public Vector3f getFront() {
        updateBasisIfNeeded();
        return new Vector3f(front);
    }

    public Vector3f getRight() {
        updateBasisIfNeeded();
        return new Vector3f(right);
    }

    public Vector3f getUp() {
        updateBasisIfNeeded();
        return new Vector3f(up);
    }

    /** Forward sur XZ seulement (pour marcher sans monter/descendre quand tu regardes haut/bas) */
    public Vector3f getForwardXZ() {
        updateBasisIfNeeded();
        Vector3f f = new Vector3f(front.x, 0.0f, front.z);
        if (f.lengthSquared() > 1e-8f) f.normalize();
        return f;
    }

    private void updateBasisIfNeeded() {
        if (!dirtyBasis) return;

        float pitch = (float) Math.toRadians(rotation.x);
        float yaw   = (float) Math.toRadians(rotation.y);

        // front:
        front.x = (float) (-Math.sin(yaw) * Math.cos(pitch));
        front.y = (float) ( Math.sin(pitch));
        front.z = (float) ( Math.cos(yaw) * Math.cos(pitch));
        front.normalize();

        // right = front x worldUp
        right.set(front).cross(0, 1, 0).normalize();

        // up = right x front
        up.set(right).cross(front).normalize();

        dirtyBasis = false;
    }

    // -------------------------
    // View matrix
    // -------------------------

    public Matrix4f getViewMatrix(Matrix4f dest) {
        updateBasisIfNeeded();
        Vector3f center = new Vector3f(position).add(front);
        return dest.identity().lookAt(position, center, up);
    }

    // -------------------------
    // Settings
    // -------------------------

    public void setSens(float sens) {
        this.sens = sens;
    }

    public float getSens() {
        return sens;
    }

    public Direction getCardinalDirection() {
        float yaw = rotation.y;

        // normaliser entre [0..360[
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 45 && yaw < 135) {
            return Direction.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return Direction.NORTH;
        } else if (yaw >= 225 && yaw < 315) {
            return Direction.EAST;
        } else {
            return Direction.SOUTH;
        }
    }
}
