package graphics;

public final class BlockMesh {
    private BlockMesh() {}

    public static final int[] FACE_IDX = { 0, 1, 2, 2, 3, 0 };

    public static final float[][][] FACE_POS = {
            // 0 NORTH (-Z)
            { {0,0,0}, {0,1,0}, {1,1,0}, {1,0,0} },

            // 1 SOUTH (+Z)
            { {1,0,1}, {1,1,1}, {0,1,1}, {0,0,1} },

            // 2 EAST  (+X)
            { {1,0,0}, {1,1,0}, {1,1,1}, {1,0,1} },

            // 3 WEST  (-X)
            { {0,0,1}, {0,1,1}, {0,1,0}, {0,0,0} },

            // 4 TOP   (+Y)
            { {0,1,1}, {1,1,1}, {1,1,0}, {0,1,0} },

            // 5 BOT   (-Y)
            { {0,0,0}, {1,0,0}, {1,0,1}, {0,0,1} },
    };

    public static final float[][] FACE_NRM = {
            { 0, 0,-1 }, // NORTH
            { 0, 0, 1 }, // SOUTH
            { 1, 0, 0 }, // EAST
            {-1, 0, 0 }, // WEST
            { 0, 1, 0 }, // TOP
            { 0,-1, 0 }, // BOTTOM
    };

    public static final int[][] NEIGHBOR = {
            { 0, 0,-1 }, // NORTH
            { 0, 0, 1 }, // SOUTH
            { 1, 0, 0 }, // EAST
            {-1, 0, 0 }, // WEST
            { 0, 1, 0 }, // TOP
            { 0,-1, 0 }, // BOTTOM
    };
}
