package game.items;

import game.blocks.BlockSettings;
import game.world.World;
import org.joml.Vector3i;

public class Item {

    private ItemSettings settings;

    public Item(ItemSettings settings) {
        this.settings = settings;
    }

    public String name() {
        return settings.name;
    }

    public int maxStack() {
        return settings.maxStack;
    }

    public int getID() {
        return settings.id;
    }

    public void setID(int id) {
        settings.id(id);
    }

    public boolean isBlockItem() {
        return settings.blockItem;
    }

    public void setBlockItem(boolean value) {
        settings.blockItem = value;
        if(settings.blockItem) {
            settings.onUse = (world, pos) -> {
                world.setBlock(settings.name + "_block", pos);
            };
        }
    }

    public int getTextureID(int face) {
        return settings.texturesID[face];
    }

    public void setTextureID(int face, int id) {
        settings.texturesID[face] = id;
    }

    public String getTextureKey(int face) {
        return settings.texturesKey[face];
    }

    public void onUse(World world, Vector3i pos) {
        settings.onUse.accept(world, pos);
    }
}
