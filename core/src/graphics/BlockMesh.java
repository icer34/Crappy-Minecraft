package graphics;

public final class BlockMesh {
    private BlockMesh() {}


    public static final int[][] EDGE_IDX = {
            {0, 1}, {0, 3}, {1, 2}, {2, 3},
            {4, 5}, {4, 7}, {5, 6}, {6, 7},
            {1, 5}, {2, 6}, {0, 4}, {3, 7}
    };

    public static final float[][] EDGE_POS = {
            {0,0,0}, {0,1,0}, {1,1,0}, {1,0,0},
            {0,0,1}, {0,1,1}, {1,1,1}, {1,0,1}
    };

    public static final int[] FACE_IDX = { 0, 1, 2, 2, 3, 0 };

    public static final int[][] NEIGHBOR = {
            { 0, 0,-1 }, // NORTH
            { 0, 0, 1 }, // SOUTH
            { 1, 0, 0 }, // EAST
            {-1, 0, 0 }, // WEST
            { 0, 1, 0 }, // TOP
            { 0,-1, 0 }, // BOTTOM
    };
}
