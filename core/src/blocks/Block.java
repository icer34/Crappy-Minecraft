package blocks;

import org.joml.Vector3i;
import world.World;

public abstract class Block implements IBlock {
    protected final String name;
    protected int id;
    protected int[] textureID = new int[6];
    protected final boolean solid;
    protected final boolean transparent;

    protected Block(String name, boolean solid, boolean transparent) {
        this.name = name;
        this.solid = solid;
        this.transparent = transparent;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public boolean isSolid() {
        return solid;
    }

    @Override
    public boolean isTransparent() {
        return transparent;
    }

    @Override
    public int getTextureID(int face) {
        return textureID[face];
    }

    @Override
    public void setTextureID(int face, int id) {
        textureID[face] = id;
    }

    @Override
    public void onPlacement(World world, Vector3i wPos) {
        world.setBlock(name, wPos);
    }

    @Override
    public abstract String textureKey(int face);
}
