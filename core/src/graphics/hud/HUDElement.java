package graphics.hud;

public abstract class HUDElement {

    protected int screenWidth;
    protected int screenHeight;

    protected float scale;

    public HUDElement(int screenWidth, int screenHeight, float scale) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scale = scale;
    }

    public abstract void draw();
    public abstract void setScale(float value);
}
