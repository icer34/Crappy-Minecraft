package blocks;

import graphics.TextureManager;

public enum BlockType {

   //                                                 n - s - e - w - t - b
    AIR_BLOCK(false, true, new int[]{-1, -1, -1, -1, -1, -1}),
    DIRT_BLOCK(true, false, new int[]{21, 21, 21, 21, 21, 21}),
    STONE_BLOCK(true, false, new int[]{4, 4, 4, 4, 4, 4}),
    GRASS_BLOCK(true, false, new int[]{1, 1, 1, 1, 3, 21}),
    WATER_BLOCK(false, false, new int[]{310, 310, 310, 310, 310, 310}),
    SAND_BLOCK(true, false, new int[]{14, 14, 14, 14, 14, 14});

    public final boolean solid;
    public final boolean transparent;

    public final int[] textureIDs; // north - south - east - west - top - bottom

    private float[][] uvCache;

    BlockType(boolean solid, boolean transparent, int[] IDs) {
        this.solid = solid;
        this.textureIDs = IDs;
        this.transparent = transparent;
    }

    public void buildCache(TextureManager atlas) {
        uvCache = new float[6][];
        for(int face = 0; face < 6; face ++) {
            float[] uv = atlas.getUVCoords(textureIDs[face]);
            uvCache[face] = new float[] {
                    uv[0], uv[3],
                    uv[0], uv[1],
                    uv[2], uv[1],
                    uv[2], uv[3],
            };
        }
    }

    public static void initTextureCaches(TextureManager atlas) {
        for(BlockType t : values()) t.buildCache(atlas);
    }

    public float[] getUV(int face) {
        return uvCache[face];
    }
}
