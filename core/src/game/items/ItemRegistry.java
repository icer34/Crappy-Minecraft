package game.items;

import game.blocks.BlockRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemRegistry {

    //Maps a block id to its instance, each link must be unique
    //(for now, one java class per block)
    // ex: 0 -> StoneBlock
    HashMap<Integer, Item> IDRegistry = new HashMap<>();

    //Maps a block name to its id, each link must be unique
    // ex: "stone_block" -> 0
    HashMap<String, Integer> nameRegistry = new HashMap<>();

    private static final AtomicInteger ID = new AtomicInteger(-1);

    public ItemRegistry(BlockRegistry blockRegistry) {
        for (Item i : ALL) {
            if (IDRegistry.containsValue(i))
                throw new RuntimeException("this item already exists in the IDRegistry: " + i.name());
            int id = ID.incrementAndGet();
            IDRegistry.put(id, i);
            nameRegistry.put(i.name(), id);
            i.setID(id);

            if(blockRegistry.blockFromName(i.name() + "_block") != null) {
                i.setBlockItem(true);
            }
        }
    }

    public Item itemFromID(int id) {
        return IDRegistry.get(id);
    }

    public int idFromName(String name) {
        if(!nameRegistry.containsKey(name)) {
            System.out.println("You can't get the id of an unregistered block: " + name);
            return -1;
        }
        return nameRegistry.get(name);
    }

    public Item itemFromName(String name) {
        return itemFromID(nameRegistry.get(name));
    }

    public Collection<Item> getItems() {
        return IDRegistry.values();
    }

//  ===================================================
//                  ITEM DEFINITIONS
//  ===================================================

    private static final Item STONE = new Item(
            new ItemSettings().def()
                    .name("stone")
                    .texturesKey("textures/block/stone.png")
    );

    private static final List<Item> ALL = List.of(STONE);
}
