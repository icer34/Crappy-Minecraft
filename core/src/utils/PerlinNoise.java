package utils;

import org.joml.Vector2f;

import static org.joml.Math.*;

public class PerlinNoise {

    private long SEED;

    public PerlinNoise(long seed) {
        this.SEED = seed;
    }

    public float fractalNoise(float x, float z, int octaves, float lacunarity, float gain) {
        float amp = 1.0f;
        float freq = 1.0f;
        float sum = 0.0f;
        float norm = 0.0f;

        for(int i = 0; i < octaves; i++) {
            sum += amp * noise(x * freq, z * freq);
            norm += amp;
            amp *= gain;
            freq *= lacunarity;
        }
        return sum / norm;
    }

    //return in [-1, 1]
    public float noise(float x, float z) {
        //cell coords
        float cellX = floor(x);
        float cellZ = floor(z);

        Vector2f[] cellPoints = new Vector2f[] {
                new Vector2f(cellX, cellZ),                //bot left
                new Vector2f(cellX, cellZ + 1),         //top left
                new Vector2f(cellX + 1, cellZ + 1),  //top right
                new Vector2f(cellX + 1, cellZ)          //bot right
        };

        Vector2f[] gradients = generateGradients(cellPoints);
        Vector2f[] centerDirs = generateDirs(cellPoints, x, z);

        float[] dotProd = new float[4];
        for(int i = 0; i < 4; i++) {
            dotProd[i] = gradients[i].dot(centerDirs[i]);
        }

        return lerp(lerp(dotProd[0], dotProd[3], smooth(x - cellX)), lerp(dotProd[1], dotProd[2], smooth(x - cellX)), smooth(z - cellZ));
    }

    private float lerp(float a, float b, float frac) {
        return a + frac * (b - a);
    }

    private float smooth(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private Vector2f[] generateDirs(Vector2f[] cellPoints, float x, float z) {
        Vector2f[] dirs = new Vector2f[4];
        for(int i = 0; i < cellPoints.length; i++) {
            dirs[i] = new Vector2f(x - cellPoints[i].x, z - cellPoints[i].y);
        }
        return dirs;
    }

    private Vector2f[] generateGradients(Vector2f[] cellPos) {
        Vector2f[] gradients = new Vector2f[4];

        for (int i = 0; i < 4; i++) {
            int ix = (int) cellPos[i].x;
            int iz = (int) cellPos[i].y;

            int h = hash2D(ix, iz, SEED);

            float r = (h & 0x7fffffff) / (float) 0x80000000;

            float a = 2.0f * (float)Math.PI * r;
            gradients[i] = new Vector2f(cos(a), sin(a));
        }
        return gradients;
    }

    private int floor(float x) {
        int ix = (int) x;
        return ix > x ? ix - 1 : ix;
    }

    private int hash2D(int x, int z, long seed) {
        int h = (int) seed;
        h ^= x * 0x27d4eb2d;
        h = Integer.rotateLeft(h, 13);
        h ^= z * 0x165667b1;
        h *= 0x85ebca6b;
        h ^= (h >>> 16);
        return h;
    }

    public void setSeed(int seed) {
        this.SEED = seed;
    }
}
