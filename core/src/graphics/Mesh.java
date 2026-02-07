package graphics;

public interface Mesh {
    void update(float[] vertices, int[] indices);
    void draw();
    void delete();
}