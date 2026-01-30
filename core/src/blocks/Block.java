package blocks;

import org.joml.Vector3i;
import world.World;

public interface Block {

    //---TEXTURES---
    String textureKey(int face);
    void setTextureID(int face, int id);
    int getTextureID(int face);

    //---PROPERTIES---
    boolean isSolid();
    boolean isTransparent();
    String name();
    void setID(int id);
    int getID();

    //---ACTIONS---
    void onPlacement(World world, Vector3i wPos);
    void onBreak(World world, Vector3i wPos);
}
