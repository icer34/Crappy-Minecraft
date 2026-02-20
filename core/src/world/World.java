package world;

import blocks.*;
import graphics.*;
import org.joml.*;
import org.w3c.dom.Text;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

import static org.joml.Math.*;

public class World {

    private final int CHUNK_SIZE = 16;
    private final int MAX_HEIGHT = 64;

    //thread pool
    private final int MAX_THREADS = 10;
    private final ExecutorService pool  = Executors.newFixedThreadPool(MAX_THREADS);

    private int RENDER_DISTANCE = 12;
    private int LOAD_DISTANCE = RENDER_DISTANCE + 1;

    private float TIME_SPEED = (float)(2 * PI / 1000.0f);

    private int seaLvl = 30;
    private boolean isUnderWater = false;

    private final TerrainGenerator terrainGenerator;
    private final BlockRegistry blockRegistry;
    private final ChunkMesher chunkMesher;

    //chunks that are ready to be rendered
    Map<Long, Chunk> chunks = new ConcurrentHashMap<>();

    //chunks requested to be rendered
    Queue<ChunkBuildResult> ready = new ConcurrentLinkedQueue<>();

    //chunks currently being loaded
    Map<Long, Boolean> inFlight = new ConcurrentHashMap<>();

    public World(long seed) {
        //setup block registry
        blockRegistry = new BlockRegistry();
        //(scan all blocks, add them to the registry) -> JSON ? java files scan ? ...?
        blockRegistry.insert(new AirBlock());
        blockRegistry.insert(new StoneBlock());
        blockRegistry.insert(new GrassBlock());
        blockRegistry.insert(new WaterBlock());

        this.chunkMesher = new ChunkMesher(CHUNK_SIZE, MAX_HEIGHT, blockRegistry);

        this.terrainGenerator = new TerrainGenerator(seed, MAX_HEIGHT, CHUNK_SIZE, blockRegistry);
    }

    public void update(Vector3f playerPos, float deltaTime) {
        isUnderWater = (playerPos.y + 1.8f <= seaLvl &&
                        playerPos.y + 1.8f > getGroundHeight((int) floor(playerPos.x), (int) floor(playerPos.z)));

        // === UPDATE LOADED CHUNKS ===
        //coords of the chunk the player is in
        int pcx = (int) floor(playerPos.x / CHUNK_SIZE);
        int pcz = (int) floor(playerPos.z / CHUNK_SIZE);

        chunks.entrySet().removeIf(entry -> {
            long id = entry.getKey();
            Chunk chunk = entry.getValue();

            int cx = chunk.getChunkX();
            int cz = chunk.getChunkZ();

            int dx = abs(cx - pcx);
            int dz = abs(cz - pcz);

            if (dx > LOAD_DISTANCE || dz > LOAD_DISTANCE) {
                remeshChunk(cx+1, cz);
                remeshChunk(cx-1, cz);
                remeshChunk(cx, cz+1);
                remeshChunk(cx, cz-1);

                chunk.cleanup();
                return true;
            }
            return false;
        });

        submitChunkLoad(pcx, pcz);

        integrateLoadedChunks();
    }

    private void submitChunkLoad(int pcx, int pcz) {
        int budget = 10;
        int submitted = 0;

        for (int r = 0; r <= LOAD_DISTANCE && submitted < budget; r++) {
            for (int dx = -r; dx <= r && submitted < budget; dx++) {
                for (int dz = -r; dz <= r && submitted < budget; dz++) {
                    if (abs(dx) != r && abs(dz) != r) continue;

                    int cx = pcx + dx;
                    int cz = pcz + dz;

                    long id = getChunkID(cx, cz);
                    if (chunks.containsKey(id)) continue;

                    if(inFlight.putIfAbsent(id, true) != null) continue;

                    pool.submit(() -> {
                        try{
                            int[] blocks = terrainGenerator.generateChunk(cx, cz);

                            ChunkMeshData mesh = chunkMesher.generateChunkMesh(chunks, blocks, cx, cz, true);

                            ready.add(new ChunkBuildResult(cx, cz, blocks, mesh));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                    submitted++;
                }
            }
        }
    }

    private void integrateLoadedChunks() {
        ChunkBuildResult result;
        while((result = ready.poll()) != null) {
            long id = getChunkID(result.chunkX(), result.chunkZ());

            Chunk c = new Chunk(result.chunkX(), result.chunkZ(), CHUNK_SIZE, MAX_HEIGHT);

            c.setBiomeMap(terrainGenerator.generateBiomeMap(c.getChunkX(), c.getChunkZ()));

            c.setBlockIDs(result.blocks());

            c.updateMeshes(result.data());

            chunks.put(id, c);

            remeshChunk(result.chunkX(), result.chunkZ());
            remeshChunk(result.chunkX()+1, result.chunkZ());
            remeshChunk(result.chunkX()-1, result.chunkZ());
            remeshChunk(result.chunkX(), result.chunkZ()+1);
            remeshChunk(result.chunkX(), result.chunkZ()-1);

            inFlight.remove(id);
        }
    }

    private void remeshChunk(int cx, int cz) {
        Chunk c = chunks.get(getChunkID(cx, cz));
        if (c == null) return;

        ChunkMeshData mesh = chunkMesher.generateChunkMesh(chunks, c.getBlocks(), cx, cz, true);

        c.updateMeshes(mesh);
    }

    public void setBlock(String blockName, Vector3i wPos) {
        if(wPos.y >= MAX_HEIGHT) return;

        int cx = (int) floor((float) wPos.x / CHUNK_SIZE);
        int cz = (int) floor((float) wPos.z / CHUNK_SIZE);

        Chunk c = chunks.get(getChunkID(cx, cz));

        Vector3i localPos = new Vector3i(wPos.x - cx * CHUNK_SIZE, wPos.y, wPos.z - cz * CHUNK_SIZE);
        c.setBlock(localPos, blockRegistry.idFromName(blockName));

        remeshChunk(cx, cz);
        remeshChunk(cx + 1, cz);
        remeshChunk(cx, cz + 1);
        remeshChunk(cx - 1, cz);
        remeshChunk(cx, cz - 1);
    }

    public void breakBlock(Vector3i wPos) {
        setBlock("air_block", wPos);
    }

    public void regenerate() {
        chunks.clear();
    }

    public void cleanup() {

        pool.shutdown();
        try {
            if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // force interruption
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        for (Chunk c : chunks.values()) {
            c.cleanup();
        }
        chunks.clear();

        ready.clear();
        inFlight.clear();
    }

    private long getChunkID(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) ^ (chunkZ & 0xffffffffL);
    }

    public void setRenderDistance(int value) {
        this.RENDER_DISTANCE = value;
        this.LOAD_DISTANCE = value;
    }

    public int getRenderDistance() {
        return this.RENDER_DISTANCE;
    }

    public float getTimeSpeed() {
        return TIME_SPEED;
    }

    public void setTimeSpeed(float TIME_SPEED) {
        this.TIME_SPEED = TIME_SPEED;
    }

    public int getLoadedChunks() {
        return chunks.size();
    }

    public int getSeaLvl() {
        return seaLvl;
    }

    public void setSeaLvl(int seaLvl) {
        this.seaLvl = seaLvl;
        terrainGenerator.setSeaLvl(seaLvl);
    }

    public int getGroundHeight(float x, float z) {
        int wx = (int) floor(x);
        int wz = (int) floor(z);

        return terrainGenerator.getHeight(wx, wz) + 1;
    }

    public Vector2i getChunkCoords(Vector3f pos) {
        int cx = (int) floor(floor(pos.x) / CHUNK_SIZE);
        int cz = (int) floor(floor(pos.z) / CHUNK_SIZE);
        return new Vector2i(cx, cz);
    }

    public Block getBlockAt(Vector3f pos) {
        Vector3i wPos = new Vector3i((int) floor(pos.x),
                                     (int) floor(pos.y),
                                     (int) floor(pos.z));

        return getBlockAt(wPos);
    }

    public byte getBiomeAt(Vector3i pos) {
        int wx = pos.x;
        int wy = pos.y;
        int wz = pos.z;

        if (wy < 0 || wy >= MAX_HEIGHT) return -1;

        int cx = (int) floor((float)wx / CHUNK_SIZE);
        int cz = (int) floor((float)wz / CHUNK_SIZE);

        Chunk c = chunks.get(getChunkID(cx, cz));
        if (c == null) return -1;

        int lx = wx - cx * CHUNK_SIZE;
        int lz = wz - cz * CHUNK_SIZE;

        return c.getBiomeAt(lx, lz);
    }

    public Block getBlockAt(Vector3i pos) {
        int wx = pos.x;
        int wy = pos.y;
        int wz = pos.z;

        if (wy < 0 || wy >= MAX_HEIGHT) return blockRegistry.blockFromName("air_block");

        int cx = (int) floor((float)wx / CHUNK_SIZE);
        int cz = (int) floor((float)wz / CHUNK_SIZE);

        Chunk c = chunks.get(getChunkID(cx, cz));
        if (c == null) return blockRegistry.blockFromName("air_block");

        int lx = wx - cx * CHUNK_SIZE;
        int lz = wz - cz * CHUNK_SIZE;

        return blockRegistry.blockFromID(c.getBlockID(lx, wy, lz));
    }

    public TerrainGenerator getGenerator() {
        return terrainGenerator;
    }

    public int getBlockID(String name) {
        return blockRegistry.idFromName(name);
    }

    public Block getBlock(String name) {
        return blockRegistry.blockFromName(name);
    }

    public BlockRegistry getBlockRegistry() {
        return blockRegistry;
    }

    public Collection<Chunk> getChunks() {
        return chunks.values();
    }

    public int getChunkSize() {
        return CHUNK_SIZE;
    }

    public boolean isPlayerUnderWater() {
        return isUnderWater;
    }
}
