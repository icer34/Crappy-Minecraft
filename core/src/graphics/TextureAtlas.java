package graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureAtlas {

    HashMap<String, Integer> texturesSlots = new HashMap<>();

    private static final AtomicInteger slotID = new AtomicInteger(-1);

    private final int SIZE;
    private final int SLOT_SIZE;
    private final int PADDING = 16;
    private final int SLOTS_PER_ROW;

    private final int atlasID;

    public TextureAtlas(int textureSize) {
        this.SLOT_SIZE = textureSize;
        if(textureSize == 16 || textureSize == 32) {
            this.SIZE = 1024;
            this.SLOTS_PER_ROW = 1024 / (SLOT_SIZE + PADDING);
        } else {
            throw new RuntimeException("Please define a supported texture size (16, 32)");
        }

        //create OpenGL texture
        this.atlasID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, atlasID);

        float max = glGetFloat(0x84FF); // GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT
        glTexParameterf(GL_TEXTURE_2D, 0x84FE, Math.min(8.0f, max)); // GL_TEXTURE_MAX_ANISOTROPY_EXT

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D,
                     0,
                     GL_RGBA8,
                     SIZE,
                     SIZE,
                     0,
                     GL_RGBA,
                     GL_UNSIGNED_BYTE,
                     (ByteBuffer) null);
    }

    public int insert(String textureKey) {
        if(texturesSlots.containsKey(textureKey)) {
            return texturesSlots.get(textureKey);
        }

        int id = slotID.incrementAndGet();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer pngData = stbi_load(textureKey, width, height, channels, 4);

            if(pngData == null) {
                System.out.println("Could not load texture: " + textureKey + ". Assigning default");

                width.clear();
                height.clear();
                channels.clear();

                pngData = stbi_load("textures/blocks/debug.png", width, height, channels, 4);

                if(pngData == null)
                    throw new RuntimeException("Failed to load the default texture - " + stbi_failure_reason());
            }

            int slotY = id / SLOTS_PER_ROW;
            int slotX = id % SLOTS_PER_ROW;

            int pixelX = slotX * (SLOT_SIZE + PADDING) + 8;
            int pixelY = slotY * (SLOT_SIZE + PADDING) + 8;

            glTexSubImage2D(GL_TEXTURE_2D,
                            0,
                            pixelX,
                            pixelY,
                            SLOT_SIZE,
                            SLOT_SIZE,
                            GL_RGBA,
                            GL_UNSIGNED_BYTE,
                            pngData);

            addTextureBorders(pngData, pixelX, pixelY);

            stbi_image_free(pngData);
        }

        texturesSlots.put(textureKey, id);
        return id;
    }

    private void addTextureBorders(ByteBuffer textureData, int x, int y) {
        int bytesPerPixel = 4; // RGBA
        int paddingPixels = 8;

        ByteBuffer topBorder = ByteBuffer.allocateDirect(SLOT_SIZE * bytesPerPixel);
        ByteBuffer bottomBorder = ByteBuffer.allocateDirect(SLOT_SIZE * bytesPerPixel);
        ByteBuffer leftBorder = ByteBuffer.allocateDirect(SLOT_SIZE * bytesPerPixel);
        ByteBuffer rightBorder = ByteBuffer.allocateDirect(SLOT_SIZE * bytesPerPixel);

        //Extract borders from texture
        for(int i = 0; i < SLOT_SIZE; i++) {
            int topIndex = i * bytesPerPixel;
            for(int c = 0; c < bytesPerPixel; c++) {
                topBorder.put(textureData.get(topIndex + c));
            }

            int bottomIndex = (SLOT_SIZE - 1) * SLOT_SIZE * bytesPerPixel + i * bytesPerPixel;
            for(int c = 0; c < bytesPerPixel; c++) {
                bottomBorder.put(textureData.get(bottomIndex + c));
            }

            int leftIndex = i * SLOT_SIZE * bytesPerPixel;
            for(int c = 0; c < bytesPerPixel; c++) {
                leftBorder.put(textureData.get(leftIndex + c));
            }

            int rightIndex = i * SLOT_SIZE * bytesPerPixel + (SLOT_SIZE - 1) * bytesPerPixel;
            for(int c = 0; c < bytesPerPixel; c++) {
                rightBorder.put(textureData.get(rightIndex + c));
            }
        }

        topBorder.flip();
        bottomBorder.flip();
        leftBorder.flip();
        rightBorder.flip();

        //copy border on padding pixels
        for(int i = 0; i < paddingPixels; i++) {
            topBorder.rewind();
            glTexSubImage2D(GL_TEXTURE_2D, 0, x, y - (i + 1), SLOT_SIZE, 1, GL_RGBA, GL_UNSIGNED_BYTE, topBorder);

            bottomBorder.rewind();
            glTexSubImage2D(GL_TEXTURE_2D, 0, x, y + SLOT_SIZE + i, SLOT_SIZE, 1, GL_RGBA, GL_UNSIGNED_BYTE, bottomBorder);

            leftBorder.rewind();
            glTexSubImage2D(GL_TEXTURE_2D, 0, x - (i + 1), y, 1, SLOT_SIZE, GL_RGBA, GL_UNSIGNED_BYTE, leftBorder);

            rightBorder.rewind();
            glTexSubImage2D(GL_TEXTURE_2D, 0, x + SLOT_SIZE + i, y, 1, SLOT_SIZE, GL_RGBA, GL_UNSIGNED_BYTE, rightBorder);
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, atlasID);
    }

    public void generateMipmaps() {
        glBindTexture(GL_TEXTURE_2D, atlasID);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void cleanup() {
        glDeleteTextures(atlasID);
    }
}
