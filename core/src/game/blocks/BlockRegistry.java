package game.blocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    public BlockRegistry() {
        for (Block b : ALL) {
            if (IDRegistry.containsValue(b))
                throw new RuntimeException("this block already exists in the IDRegistry: " + b.name());
            int id = ID.incrementAndGet();
            IDRegistry.put(id, b);
            nameRegistry.put(b.name(), id);
            b.setID(id);
        }
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

//  =================
//  BLOCK DEFINITIONS
//  =================

    private static final Block AIR = new Block(
            new BlockSettings().def()
                    .name("air_block")
                    .solid(false)
                    .transparent(true)
                    .baseTextureKey("textures/particle/big_smoke_11.png")
    );

    private static final Block DIRT = new Block(
            new BlockSettings().def()
                    .name("dirt_block")
                    .baseTextureKey("textures/block/dirt.png")
    );

    private static final Block GRASS = new Block(
            new BlockSettings().def()
                    .name("grass_block")
                    .baseTextureKey("textures/block/dirt.png")
                    .ovrTexturesKey("textures/block/grass_block_side_overlay.png",
                                    "textures/block/grass_block_side_overlay.png",
                                    "textures/block/grass_block_side_overlay.png",
                                    "textures/block/grass_block_side_overlay.png",
                                    "textures/block/grass_block_top.png")
    );

    private static final Block STONE = new Block(
            new BlockSettings().def()
                    .name("stone_block")
                    .baseTextureKey("textures/block/stone.png")
    );

    private static final Block WATER = new Block(
            new BlockSettings().def()
                    .name("water_block")
                    .baseTextureKey("textures/block/water_still.png")
                    .solid(false)
    );

    private static final List<Block> ALL = List.of(AIR, DIRT, GRASS, STONE, WATER);
}
