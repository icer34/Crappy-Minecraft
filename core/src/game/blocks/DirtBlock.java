package game.blocks;

import org.joml.Vector3i;
import game.world.World;

public class DirtBlock extends Block implements BreakableBlock {

    public DirtBlock() {
        super("dirt_block", true, false);
    }

    @Override
    public String textureKey(int face) {
        return "textures/block/dirt.png";
    }

    @Override
    public void onBreak(World world, Vector3i wPos) {
        world.breakBlock(wPos);
    }
}
