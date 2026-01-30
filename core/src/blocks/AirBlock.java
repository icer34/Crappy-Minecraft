package blocks;

import org.joml.Vector3i;
import world.World;

public class AirBlock implements Block {

    private final String name = "air_block";
    private int ID;

    private final boolean SOLID = false;
    private final boolean TRANSPARENT = true;

    private int[] textureID = new int[6];

    @Override
    public void setID(int id) {
        this.ID = id;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String textureKey(int face) {
        return "textures/particle/big_smoke_11.png";
    }

    @Override
    public void setTextureID(int face, int id) {
        this.textureID[face] = id;
    }

    @Override
    public int getTextureID(int face) {
        return textureID[face];
    }

    @Override
    public String name() {
        return name;
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
    public void onPlacement(World world, Vector3i wPos) {
        //world.setBlock(ID, wPos);
    }

    @Override
    public void onBreak(World world, Vector3i wPos) {
        world.breakBlock(wPos);
    }
}
