package game.blocks;

import org.joml.Vector3i;
import game.world.World;

public class StoneBlock extends Block
                        implements BreakableBlock {

    public StoneBlock() {
        super("stone_block", true, false);
    }

    @Override
    public String textureKey(int face) {
        return "textures/block/stone.png";
    }

    @Override
    public void onBreak(World world, Vector3i wPos) {
        world.breakBlock(wPos);
    }
}
