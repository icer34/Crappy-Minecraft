package graphics.hud;

import graphics.mesh.HUDMesh;
import graphics.mesh.MeshData;

public class Hotbar extends HUDElement {

    private HUDTexture texture;
    private HUDMesh mesh;

    private float width;
    private float height;
    private float scale = 2;

    public Hotbar(int screenWidth, int screenHeight, String texturePath) {
        super(screenWidth, screenHeight);
        this.texture = new HUDTexture(texturePath);
        this.mesh = new HUDMesh(createMeshData());
    }

    @Override
    public void draw() {
        texture.load();
        mesh.draw();
    }

    private MeshData createMeshData() {
        return null;
    }
}
