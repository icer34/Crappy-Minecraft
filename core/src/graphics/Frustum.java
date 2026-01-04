package graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import world.Chunk;

import java.util.Map;

import static org.joml.Math.*;

public class Frustum {

    private final Matrix4f proj = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4f clip = new Matrix4f();

    private final Vector3f pv = new Vector3f();

    // near - far - left - right - top - bottom
    private Vector4f[] planes = new Vector4f[6];

    public void cull(Map<Long, Chunk> chunks) {
        for(long key : chunks.keySet()) {
            Chunk c = chunks.get(key);

            Vector3f minBox = new Vector3f(c.getChunkX() * c.getSize(), 0.0f, c.getChunkZ() * c.getSize());
            Vector3f maxBox = new Vector3f((c.getChunkX() + 1) * c.getSize(), c.getMaxHeight(), (c.getChunkZ() + 1) * c.getSize());

            boolean visible = isBoxInFrustum(minBox, maxBox);

            c.setVisible(visible);
        }
    }

    public void update(Matrix4f proj, Matrix4f view) {

        // clip = P * V
        proj.mul(view, clip);

        for (int i = 0; i < 6; i++) if (planes[i] == null) planes[i] = new Vector4f();

        extractPlane(planes[2], clip.m03() + clip.m00(), clip.m13() + clip.m10(), clip.m23() + clip.m20(), clip.m33() + clip.m30()); // LEFT
        extractPlane(planes[3], clip.m03() - clip.m00(), clip.m13() - clip.m10(), clip.m23() - clip.m20(), clip.m33() - clip.m30()); // RIGHT
        extractPlane(planes[5], clip.m03() + clip.m01(), clip.m13() + clip.m11(), clip.m23() + clip.m21(), clip.m33() + clip.m31()); // BOTTOM
        extractPlane(planes[4], clip.m03() - clip.m01(), clip.m13() - clip.m11(), clip.m23() - clip.m21(), clip.m33() - clip.m31()); // TOP
        extractPlane(planes[0], clip.m03() + clip.m02(), clip.m13() + clip.m12(), clip.m23() + clip.m22(), clip.m33() + clip.m32()); // NEAR
        extractPlane(planes[1], clip.m03() - clip.m02(), clip.m13() - clip.m12(), clip.m23() - clip.m22(), clip.m33() - clip.m32()); // FAR
    }

    private void extractPlane(Vector4f out, float a, float b, float c, float d) {
        // normalise (a,b,c)
        float invLen = 1.0f / (float) java.lang.Math.sqrt(a*a + b*b + c*c);
        out.set(a * invLen, b * invLen, c * invLen, d * invLen);
    }

    private boolean isBoxInFrustum(Vector3f min, Vector3f max) {
        for (Vector4f plane : planes) {
            Vector3f positiveVertex = new Vector3f(
                    plane.x > 0 ? max.x : min.x,
                    plane.y > 0 ? max.y : min.y,
                    plane.z > 0 ? max.z : min.z
            );

            float distance = plane.x * positiveVertex.x +
                    plane.y * positiveVertex.y +
                    plane.z * positiveVertex.z + plane.w;

            if (distance < 0) {
                return false;
            }
        }
        return true;
    }
}
