package graphics.hud;

import core.Window;
import graphics.mesh.Mesh;

import java.util.ArrayList;

public class HUD {

    private HUDMesh crosshairMesh;
    private int crosshairSize = 15;

    public HUD(Window window) {
        //create crosshair mesh and set its data
        crosshairMesh = new HUDMesh();
        initCrosshairMesh(crosshairMesh, window);
    }

    public void draw() {
        crosshairMesh.draw();
    }

    private void initCrosshairMesh(Mesh mesh, Window window) {
        ArrayList<Integer> indices = new ArrayList<>();

        //get the center of the window
        float cx = window.getWidth() / 2f;
        float cy = window.getHeight() / 2f;

        //TODO
    }
}
