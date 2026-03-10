package game.blocks;

import org.joml.Vector3i;
import game.world.World;
import utils.Faces;

public class GrassBlock extends Block
                        implements BreakableBlock, MultiTexturedBlock {

    private int[] ovrTextureID = new int[6];

    public GrassBlock() {
        super("grass_block", true, false);
    }

    @Override
    public void onBreak(World world, Vector3i wPos) {
        world.breakBlock(wPos);
    }

    @Override
    public String textureKey(int face) {
        return "textures/block/dirt.png";
    }

    @Override
    public String ovrTextureKey(int face) {
        return switch (face) {
            case Faces.NORTH, Faces.SOUTH, Faces.EAST, Faces.WEST -> "textures/block/grass_block_side_overlay.png";
            case Faces.TOP -> "textures/block/grass_block_top.png";
            case Faces.BOTTOM -> "textures/block/dirt.png";
            default -> "";
        };
    }

    @Override
    public void setOvrTextureID(int face, int id) {
        ovrTextureID[face] = id;
    }

    @Override
    public int getOvrTextureID(int face) {
        return ovrTextureID[face];
    }
}
