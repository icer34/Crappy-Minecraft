package graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureManager {

    private final int TEXTURE_SIZE;

    private int atlasID;
    private int atlasWidth;
    private int atlasHeight;

    public TextureManager(String atlasPath, int textureSize) {
        this.TEXTURE_SIZE = textureSize;

        //load atlas in opengl
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer imageData = stbi_load(atlasPath, width, height, channels, 4);

            if (imageData == null) {
                throw new RuntimeException("Failed to load texture: " + stbi_failure_reason());
            }

            this.atlasWidth = width.get(0);
            this.atlasHeight = height.get(0);

            this.atlasID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, atlasID);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, atlasWidth, atlasHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);

            stbi_image_free(imageData);
        }
    }

    public float[] getUVCoords(int textureID) {
        int texturesPerRow = atlasWidth / TEXTURE_SIZE;

        int row = textureID / texturesPerRow;
        int col = textureID % texturesPerRow;

        float minU = (col * (float)TEXTURE_SIZE) / atlasWidth;
        float maxU = ((col + 1) * (float)TEXTURE_SIZE) / atlasWidth;

        float minV = (row * (float)TEXTURE_SIZE) / atlasHeight;
        float maxV = ((row + 1) * (float)TEXTURE_SIZE) / atlasHeight;

        return new float[]{minU, minV, maxU, maxV};
    }

    public void bindAtlas() {
        glBindTexture(GL_TEXTURE_2D, atlasID);
    }

    public void cleanup() {
        glDeleteTextures(atlasID);
    }
}
