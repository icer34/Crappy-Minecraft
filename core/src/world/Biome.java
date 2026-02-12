package world;

public enum Biome {

    PLAINS((byte) 0, 0x326632, 0x1f1f8f),
    DESERT((byte) 1, 0xFFFFFF, 0x1f1f8f);

    public final byte id;
    public final int grassRGB;
    public final int waterRGB;

    Biome(byte id, int grassRGB, int waterRGB) {
        this.id = id;
        this.grassRGB = grassRGB;
        this.waterRGB = waterRGB;
    }
}
