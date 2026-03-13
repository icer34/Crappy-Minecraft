package game.items;

import game.world.World;
import org.joml.Vector3i;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItemSettings {
    //default
    String name = "default";
    int maxStack = 64;
    int id = -1;
    boolean blockItem = false;
    String[] texturesKey = new String[6];
    int[] texturesID = new int[6];

    public ItemSettings def() {
        return this;
    }

    public ItemSettings name(String name) {
        this.name = name;
        return this;
    }

    public ItemSettings maxStack(int value) {
        this.maxStack = value;
        return this;
    }

    public ItemSettings texturesKey(String ... textures) {
        int l = textures.length;
        for(int i = 0; i < l; i++) {
            this.texturesKey[i] = textures[i];
        }
        return this;
    }

    public ItemSettings id(int value) {
        this.id = value;
        return this;
    }

//    =========================================================================
//    =========================================================================

    //    default methods
    BiConsumer<World, Vector3i> onUse = (world, pos) -> {};

    public ItemSettings onUse(BiConsumer<World, Vector3i> method) {
        this.onUse = method;
        return this;
    }
}
