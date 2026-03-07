package graphics.hud;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static  org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.stbi_load;

public class HUDTexture {

    private final int texID;

    private final int width;
    private final int height;

    public HUDTexture (String path) {
        this.texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer pngData = stbi_load(path, width, height, channels, 4);

            this.width = width.get();
            this.height = height.get();

            if(pngData == null) {
                System.out.println("Failed to load the HUD texture: " + path);
                return;
            }

            glTexImage2D(GL_TEXTURE_2D,
                         0,
                         GL_RGBA,
                         this.width, this.height,
                         0,
                         GL_RGBA,
                         GL_UNSIGNED_BYTE,
                         pngData);

            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    public void load() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
