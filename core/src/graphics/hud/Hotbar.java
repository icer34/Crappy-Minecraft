package graphics.hud;

import graphics.mesh.HUDMesh;
import graphics.mesh.MeshData;

public class Hotbar extends HUDElement {

    private HUDTexture texture;
    private HUDMesh mesh;

    private float width;
    private float height;
    private float scale;
    private float pos[];

    public Hotbar(int screenWidth, int screenHeight, float scale, String texturePath) {
        super(screenWidth, screenHeight, scale);

        this.scale = scale;

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

    @Override
    public void setScale(float value) {
        this.scale = value;
        this.mesh.update(createMeshData());
    }

    private MeshData createMeshData() {
        float cx = screenWidth / 2f;

        float x0 = cx - (width * scale) / 2f;
        float x1 = cx + (width * scale) / 2f;
        float y0 = screenHeight - (height * scale) - 10;
        float y1 = screenHeight - 10;

        float[] vertices = new float[] {x0, y0, 0f, 0f, //top left
                                        x1, y0, 1f, 0f, //top right
                                        x0, y1, 0f, 1f, //bottom left
                                        x1, y1, 1f, 1f};//bottom right

        int[] indices = new int[] {0, 3, 2,
                                   0, 1, 3};

        this.pos = new float[] {x0, y0, x1, y1};

        return new MeshData(vertices, indices);
    }

    public int getSlotSize() {
        return (int) ((width / 9.0f ) * scale);
    }

    public int[][] getSlotCenters() {
        int slotSize = (int) ((width / 9.0f) * scale);
        int[][] centers = new int[9][2];
        for(int i = 0; i < 9; i++) {
            centers[i][0] = (int) pos[0] + i * slotSize - (slotSize / 2);
            centers[i][1] = (int) pos[1] + (int) height / 2;
        }

        return centers;
    }
}
