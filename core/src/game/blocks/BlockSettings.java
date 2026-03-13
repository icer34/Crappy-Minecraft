package game.blocks;

import game.world.World;
import org.joml.Vector3i;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class BlockSettings {

//    default settings
    String name = "default";
    int id = -1;
    boolean solid = true;
    boolean transparent = false;
    boolean multiTextured = false;
    String[] baseTexturesKey = new String[6];
    int[]  baseTexturesID = new int[6];
    String[] ovrTexturesKey = new String[6];
    int[] ovrTexturesID = new int[6];

    public BlockSettings def() {
        return new BlockSettings();
    }

    public BlockSettings name(String name) {
        this.name = name;
        return this;
    }

    public BlockSettings id(int value) {
        this.id = value;
        return this;
    }

    public BlockSettings solid(boolean value) {
        this.solid = value;
        return this;
    }

    public BlockSettings transparent(boolean value) {
        this.transparent = value;
        return this;
    }

    public int getBaseTextureID(int face) {
        return baseTexturesID[face];
    }

    public void setBaseTexturesID(int face, int id) {
        this.baseTexturesID[face] = id;
    }

    public int getOvrTextureID(int face) {
        return ovrTexturesID[face];
    }

    public void setOvrTexturesID(int face, int id) {
        this.ovrTexturesID[face] = id;
    }

    public BlockSettings baseTextureKey(String texture) {
        Arrays.fill(baseTexturesKey, texture);
        return this;
    }

    public BlockSettings baseTexturesKey(String[] textures) {
        this.baseTexturesKey = textures;
        return this;
    }

    public BlockSettings ovrTextureKey(String texture) {
        Arrays.fill(ovrTexturesKey, texture);
        this.multiTextured = true;
        return this;
    }

    public BlockSettings ovrTexturesKey(String[] textures) {
        this.ovrTexturesKey = textures;
        this.multiTextured = true;
        return this;
    }

//    Default methods for when a block is:
//       - placed
//       - broken
//       - interacted with
    BiConsumer<World, Vector3i> onPlacement = (world, pos) -> {
        world.setBlock(this.name, pos);
    };
    BiConsumer<World, Vector3i> onBreak = (world, pos) -> {
        world.breakBlock(pos);
    };
    BiConsumer<World, Vector3i> onInteract = (world, pos) -> {};

    public BlockSettings onBreak(BiConsumer<World, Vector3i> method) {
        this.onBreak = method;
        return this;
    }

    public BlockSettings onPlacement(BiConsumer<World, Vector3i> method) {
        this.onPlacement = method;
        return this;
    }

    public BlockSettings onInteract(BiConsumer<World, Vector3i> method) {
        this.onInteract = method;
        return this;
    }
}
