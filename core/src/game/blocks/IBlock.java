package game.blocks;

import org.joml.Vector3i;
import game.world.World;

public interface IBlock {
    //Properties shared amongst all blocks
    String name();
    int getID();
    void setID(int id);
    boolean isSolid();
    boolean isTransparent();

    //Methods shared amongst all blocks
    String textureKey(int face);
    int getTextureID(int face);
    void setTextureID(int face, int id);
    void onPlacement(World world, Vector3i wPos);
}
