package game.blocks;

public class Block {

    private BlockSettings settings;

    public Block(BlockSettings settings) {
        this.settings = settings;
    }

    public String name() {
        return settings.name;
    }

    public int getID() {
        return settings.id;
    }

    public void setID(int id) {
        settings.id(id);
    }

    public boolean isSolid() {
        return settings.solid;
    }

    public boolean isTransparent() {
        return settings.transparent;
    }

    public boolean isMultitextured() {
        return settings.multiTextured;
    }

    public int getBaseTextureID(int face) {
        return settings.getBaseTextureID(face);
    }

    public void setBaseTextureID(int face, int id) {
        settings.setBaseTexturesID(face, id);
    }

    public int getOvrTextureID(int face) {
        return settings.getOvrTextureID(face);
    }

    public void setOvrTextureID(int face, int id) {
        settings.setOvrTexturesID(face, id);
    }
}
