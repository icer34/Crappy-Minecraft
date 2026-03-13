package game;

import game.items.Item;

import java.util.Arrays;

public class Inventory {

    //0 - 8 --> hotbar
    //9 - 35 --> basic inventory
    //36 - 39 --> armor slots
    //40 - 44 --> crafting slots
    private SlotData[] slots = new SlotData[44];

    public SlotData getSlotData(int slot) {
        return slots[slot];
    }

    public SlotData[] getHotbarData() {
        return Arrays.copyOfRange(slots, 0, 9);
    }

    public void put(Item item, int count, int slot) {
        if(count > item.maxStack()) return;

        slots[slot] = new SlotData(item.getID(), count);
    }

    public void put(Item item, int count) {
        //TODO: get the first available slot to place the item
    }

    public int getItemID(int slot) {
        return slots[slot].itemID();
    }
}
