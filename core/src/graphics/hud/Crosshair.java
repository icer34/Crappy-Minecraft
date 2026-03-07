package graphics.hud;

import graphics.mesh.HUDMesh;
import graphics.mesh.MeshData;

public class Crosshair extends HUDElement {

    private HUDTexture texture;
    private HUDMesh mesh;

    private float width;
    private float height;
    private float scale = 2f;

    public Crosshair(int screenWidth, int screenHeight, String texturePath) {
        super(screenWidth, screenHeight);

        this.texture = new HUDTexture(texturePath);
        this.width = texture.getWidth();
        this.height = texture.getHeight();

        this.mesh = new HUDMesh(createMeshData());
    }

    @Override
    public void draw() {
        texture.load();
        mesh.draw();
    }

    private MeshData createMeshData() {
        //get the center of the window
        float cx = screenWidth / 2f;
        float cy = screenHeight / 2f;

        float x0 = cx - (width * scale) / 2f;
        float x1 = cx + (width * scale) / 2f;
        float y0 = cy - (height * scale) / 2f;
        float y1 = cy + (height * scale) / 2f;

        float[] vertices = new float[] {x0, y0, 0f, 0f, //top left
                                        x1, y0, 1f, 0f, //top right
                                        x0, y1, 0f, 1f, //bottom left
                                        x1, y1, 1f, 1f};//bottom right

        int[] indices = new int[] {0, 3, 2,
                                   0, 1, 3};

        return new MeshData(vertices, indices);
    }
}
