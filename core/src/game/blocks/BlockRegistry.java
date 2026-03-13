package game.blocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockRegistry {

    //Maps a block id to its instance, each link must be unique
    //(for now, one java class per block)
    // ex: 0 -> StoneBlock
    HashMap<Integer, Block> IDRegistry = new HashMap<>();

    //Maps a block name to its id, each link must be unique
    // ex: "stone_block" -> 0
    HashMap<String, Integer> nameRegistry = new HashMap<>();

    private static final AtomicInteger ID = new AtomicInteger(-1);

    public void insert(Block block) {
        if(IDRegistry.containsValue(block))
            throw new RuntimeException("this block already exists in the IDRegistry: " + block.name());
        int id = ID.incrementAndGet();
        IDRegistry.put(id, block);
        nameRegistry.put(block.name(), id);
        block.setID(id);
    }

    public Block blockFromID(int id) {
        return IDRegistry.get(id);
    }

    public int idFromName(String name) {
        if(!nameRegistry.containsKey(name)) {
            System.out.println("You can't get the id of an unregistered block: " + name);
            return -1;
        }
        return nameRegistry.get(name);
    }

    public Block blockFromName(String name) {
        return blockFromID(nameRegistry.get(name));
    }

    public Collection<Block> getBlocks() {
        return IDRegistry.values();
    }

//    =================
//    BLOCK DEFINITIONS
//    =================
    //todo
}
