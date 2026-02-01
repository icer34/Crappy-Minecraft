package blocks;

import org.joml.Vector3i;
import utils.Faces;
import world.World;

public class GrassBlock implements Block {

    private final String name = "grass_block";
    private int ID;

    private final boolean SOLID = true;
    private final boolean TRANSPARENT = false;

    private int[] textureID = new int[6];

    @Override
    public String textureKey(int face) {
        return switch (face) {
            case Faces.NORTH, Faces.SOUTH, Faces.EAST, Faces.WEST -> "textures/block/grass_block_side.png";
            case Faces.TOP -> "textures/block/grass_block_top.png";
            case Faces.BOTTOM -> "textures/block/dirt.png";
            default -> throw new RuntimeException("Invalid texture key given: " + face);
        };
    }

    @Override
    public void setTextureID(int face, int id) {
        textureID[face] = id;
    }

    @Override
    public int getTextureID(int face) {
        return textureID[face];
    }

    @Override
    public boolean isSolid() {
        return SOLID;
    }

    @Override
    public boolean isTransparent() {
        return TRANSPARENT;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void setID(int id) {
        this.ID = id;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void onPlacement(World world, Vector3i wPos) {
        world.setBlock(name, wPos);
    }

    @Override
    public void onBreak(World world, Vector3i wPos) {
        world.breakBlock(wPos);
    }
}
