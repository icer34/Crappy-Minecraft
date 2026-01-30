package utils;

import blocks.Block;
import org.joml.Vector3f;
import org.joml.Vector3i;
import world.World;

import static org.joml.Math.*;

public class RayCaster {

    private World world;

    public RayCaster(World world) {
        this.world = world;
    }

    public RayCastResult castRay(Vector3f origin, Vector3f dir, float reach) {

        Vector3f d = new Vector3f(dir);
        if (d.lengthSquared() == 0.0f) {
            return new RayCastResult(false, new Vector3i(), world.getBlockID("air_block"), new Vector3i());
        }
        d.normalize();

        // Voxel de départ
        int x = (int) floor(origin.x);
        int y = (int) floor(origin.y);
        int z = (int) floor(origin.z);

        // Step selon le signe de la direction
        int stepX = d.x > 0 ? 1 : (d.x < 0 ? -1 : 0);
        int stepY = d.y > 0 ? 1 : (d.y < 0 ? -1 : 0);
        int stepZ = d.z > 0 ? 1 : (d.z < 0 ? -1 : 0);

        // tDelta = "distance" (paramétrique) entre deux intersections de faces sur un axe
        float tDeltaX = (stepX == 0) ? Float.POSITIVE_INFINITY : abs(1.0f / d.x);
        float tDeltaY = (stepY == 0) ? Float.POSITIVE_INFINITY : abs(1.0f / d.y);
        float tDeltaZ = (stepZ == 0) ? Float.POSITIVE_INFINITY : abs(1.0f / d.z);

        // tMax = distance paramétrique jusqu'à la prochaine face sur chaque axe
        float nextVoxelBoundaryX = (stepX > 0) ? (x + 1.0f) : (float) x;
        float nextVoxelBoundaryY = (stepY > 0) ? (y + 1.0f) : (float) y;
        float nextVoxelBoundaryZ = (stepZ > 0) ? (z + 1.0f) : (float) z;

        float tMaxX = (stepX == 0) ? Float.POSITIVE_INFINITY : (nextVoxelBoundaryX - origin.x) / d.x;
        float tMaxY = (stepY == 0) ? Float.POSITIVE_INFINITY : (nextVoxelBoundaryY - origin.y) / d.y;
        float tMaxZ = (stepZ == 0) ? Float.POSITIVE_INFINITY : (nextVoxelBoundaryZ - origin.z) / d.z;

        Vector3i hitNormal = new Vector3i(0, 0, 0);

        float t = 0.0f;
        while (t <= reach) {

            Block block = world.getBlockAt(new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f));
            if (block.isSolid()) {
                return new RayCastResult(true, new Vector3i(x, y, z), block.getID(), new Vector3i(hitNormal));
            }

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                x += stepX;
                t = tMaxX;
                tMaxX += tDeltaX;
                hitNormal.set(-stepX, 0, 0);
            } else if (tMaxY < tMaxZ) {
                y += stepY;
                t = tMaxY;
                tMaxY += tDeltaY;
                hitNormal.set(0, -stepY, 0);
            } else {
                z += stepZ;
                t = tMaxZ;
                tMaxZ += tDeltaZ;
                hitNormal.set(0, 0, -stepZ);
            }
        }

        return new RayCastResult(false, new Vector3i(), world.getBlockID("air_block"), new Vector3i());
    }
}
