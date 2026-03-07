package graphics.hud;

public abstract class HUDElement {

    protected int screenWidth;
    protected int screenHeight;

    public HUDElement(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public abstract void draw();
}
