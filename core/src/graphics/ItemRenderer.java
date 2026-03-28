package graphics;

import game.SlotData;
import game.items.Item;
import game.items.ItemRegistry;

public class ItemRenderer {

    ItemRegistry registry;

    public ItemRenderer(ItemRegistry itemRegistry) {
        this.registry = itemRegistry;
    }

    public void renderHotbar(SlotData[] hotbarItems, int slotSize, int[][] slotCenters) {
        for(int i = 0; i < 9; i++) {
            if(hotbarItems[i] == null) continue;
            int itemID = hotbarItems[i].itemID();
            int itemCount = hotbarItems[i].count();
            Item item = registry.itemFromID(itemID);
            //TODO;
        }
    }
}
