package blocks;

public interface MultiTexturedBlock {
    String ovrTextureKey(int face);
    void setOvrTextureID(int face, int id);
    int getOvrTextureID(int face);
}
