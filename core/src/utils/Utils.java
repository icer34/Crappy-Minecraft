package utils;

import blocks.BlockType;
import graphics.TextureManager;
import world.TerrainGenerator;

public final class Utils {

    public static TextureManager BLOCK_ATLAS;
    public static TerrainGenerator TERRAIN_GENERATOR;

    private Utils(){}

    public static void init() {
        BLOCK_ATLAS = new TextureManager("textures/block_atlas.png", 16);
        BlockType.initTextureCaches(BLOCK_ATLAS);

        TERRAIN_GENERATOR = new TerrainGenerator();
    }

    public static void cleanup() {
        if(BLOCK_ATLAS != null) BLOCK_ATLAS.cleanup();
    }
}
