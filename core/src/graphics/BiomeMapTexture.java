package graphics;

import org.lwjgl.BufferUtils;
import game.world.Chunk;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public class BiomeMapTexture {

    private final int texID;
    private final ByteBuffer buff = BufferUtils.createByteBuffer(256 * 256);

    public BiomeMapTexture() {
        this.texID = glGenTextures();
        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexImage2D(GL_TEXTURE_2D,
                     0,
                     GL_R8UI,
                     16,
                     16,
                     0,
                     GL_RED_INTEGER,
                     GL_UNSIGNED_BYTE,
                     (ByteBuffer) null);

    }

    public void update(Chunk c) {
        int chunkSize = c.getSize();
        byte[][] biomeMap = c.getBiomeMap();

        for(int i = 0; i < chunkSize; i++) {
            buff.put(biomeMap[i], 0, chunkSize);
        }

        buff.flip();

        glTexSubImage2D(GL_TEXTURE_2D,
                        0,
                        0, 0,
                        chunkSize, chunkSize,
                        GL_RED_INTEGER,
                        GL_UNSIGNED_BYTE,
                        buff);

        buff.clear();
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }
}
