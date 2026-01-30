package world;

import utils.PerlinNoise;

import java.util.Arrays;

public class TerrainGenerator {

    private long seed = 0;
    private int MAX_TERRAIN_HEIGHT = 0;
    private int CHUNK_SIZE = 0;

    private int octaves = 5;
    private float lacunarity = 2.0f;
    private float gain = 0.5f;
    private float scale = 0.008f;
    private int seaLvl = 30;

    private PerlinNoise terrainNoise;

    private BlockRegistry blockRegistry;

    public TerrainGenerator(long seed, int maxHeight, int size, BlockRegistry registry){
        this.seed = seed;
        this.MAX_TERRAIN_HEIGHT = maxHeight;
        this.CHUNK_SIZE = size;
        this.terrainNoise = new PerlinNoise(seed);
        this.blockRegistry = registry;
    }

    public int[] generateChunk(int cx, int cz) {
        int[] blocks = new int[CHUNK_SIZE * CHUNK_SIZE * MAX_TERRAIN_HEIGHT];

        int baseX = cx * CHUNK_SIZE;
        int baseZ = cz * CHUNK_SIZE;

        for (int x = 0; x < CHUNK_SIZE; x++) {
            int worldX = baseX + x;

            for (int z = 0; z < CHUNK_SIZE; z++) {
                int worldZ = baseZ + z;

                int h = getHeight(worldX, worldZ);

                if (h < 0) h = 0;
                if (h >= MAX_TERRAIN_HEIGHT) h = MAX_TERRAIN_HEIGHT - 1;

                for (int y = 0; y <= h; y++) {
                    int idx = getIdx(x, y, z);

                    if (y == h) {
                        if(y == seaLvl || y == seaLvl - 1)
                            blocks[idx] = blockRegistry.idFromName("stone_block");
                        else
                            blocks[idx] = blockRegistry.idFromName("stone_block");
                    }

                    else if (y > h - 3) blocks[idx] = blockRegistry.idFromName("stone_block");
                    else blocks[idx] = blockRegistry.idFromName("stone_block");
                }

                for(int y = h + 1; y < MAX_TERRAIN_HEIGHT; y++) {
                    blocks[getIdx(x, y, z)] = blockRegistry.idFromName("air_block");
                }
            }
        }

        return blocks;
    }


    public int getHeight(int wx, int wz) {
        float noise = terrainNoise.fractalNoise(wx * scale, wz * scale, octaves, lacunarity, gain);
        int base = MAX_TERRAIN_HEIGHT / 2;
        int amp = 70;
        return (int) (base + amp * noise);
    }

    private int getIdx(int x, int y, int z) {
        return x + y * CHUNK_SIZE + z * CHUNK_SIZE * MAX_TERRAIN_HEIGHT;
    }

    public int getOctaves() {
        return octaves;
    }

    public float getLacunarity() {
        return lacunarity;
    }

    public float getGain() {
        return gain;
    }

    public float getScale() {
        return scale;
    }

    public int getSeed() {
        return (int) seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
        terrainNoise.setSeed(seed);
    }

    public int getSeaLvl() {
        return seaLvl;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setLacunarity(float lacunarity) {
        this.lacunarity = lacunarity;
    }

    public void setOctaves(int octaves) {
        this.octaves = octaves;
    }

    public void setSeaLvl(int seaLvl) {
        this.seaLvl = seaLvl;
    }
}
