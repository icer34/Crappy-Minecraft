package graphics;

import org.lwjgl.BufferUtils;
import world.Biome;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public class TintTexture {

    private final int texID;

    public TintTexture() {
        this.texID = glGenTextures();
        bind();

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        Biome[] biomes = Biome.values();
        int n = biomes.length;
        int rows = 2;
        ByteBuffer buff = BufferUtils.createByteBuffer(n * rows * 3);

        for(int y = 0; y < rows; y++) {
            for(Biome b : biomes) {
                int rgb = switch (y) {
                    case 0 -> b.grassRGB;
                    //could add more tints per biome ...
                    default -> b.waterRGB;
                };
                putRGB(buff, rgb);
            }
        }
        buff.flip();

        glTexImage2D(GL_TEXTURE_2D,
                     0,
                     GL_RGB8,
                     n, rows,
                     0,
                     GL_RGB,
                     GL_UNSIGNED_BYTE,
                     buff);
    }

    private void putRGB(ByteBuffer buff, int rgb) {
        buff.put((byte) ((rgb >> 16) & 0xFF));
        buff.put((byte) ((rgb >> 8) & 0xFF));
        buff.put((byte) (rgb & 0xFF));
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }
}
