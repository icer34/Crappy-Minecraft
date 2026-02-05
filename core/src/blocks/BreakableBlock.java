package blocks;

import org.joml.Vector3i;
import world.World;

public interface BreakableBlock {
    void onBreak(World world, Vector3i wPos);
}
