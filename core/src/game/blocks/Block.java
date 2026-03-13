package game.blocks;

import game.world.World;
import org.joml.Vector3i;

public class Block {

    private BlockSettings settings;

    public Block(BlockSettings settings) {
        this.settings = settings;
    }

    public String name() {
        return settings.name;
    }

    public int getID() {
        return settings.id;
    }

    public void setID(int id) {
        settings.id(id);
    }

    public boolean isSolid() {
        return settings.solid;
    }

    public boolean isTransparent() {
        return settings.transparent;
    }

    public boolean isMultitextured() {
        return settings.multiTextured;
    }

    public boolean isBreakable() {
        return settings.breakable;
    }

    public int getBaseTextureID(int face) {
        return settings.baseTexturesID[face];
    }

    public void setBaseTextureID(int face, int id) {
        settings.baseTexturesID[face] = id;
    }

    public int getOvrTextureID(int face) {
        return settings.ovrTexturesID[face];
    }

    public void setOvrTextureID(int face, int id) {
        settings.ovrTexturesID[face] = id;
    }

    public String getBaseTextureKey(int face) {
        return settings.baseTexturesKey[face];
    }

    public String getOvrTextureKey(int face) {
        return settings.ovrTexturesKey[face];
    }

    public void onBreak(World world, Vector3i pos) {
        settings.onBreak.accept(world, pos);
    }

    public void onPlacement(World world, Vector3i pos) {
        settings.onPlacement.accept(world, pos);
    }

    public void onInteract(World world, Vector3i pos) {
        settings.onInteract.accept(world, pos);
    }
}
