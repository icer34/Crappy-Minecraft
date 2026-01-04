package core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import utils.Input;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL30.*;

public class Window {
    private final String name;

    private int width;
    private int height;

    private boolean vSync;

    private long windowHandle;

    private boolean cursorToggle = false;

    public Window(String name, int w, int h, boolean vSync) {
        this.name = name;
        this.width = w;
        this.height = h;
        this.vSync = vSync;
    }

    public void init(Input input) {
        if(!glfwInit()) {
            System.out.println("Could not initialize GLFW");
        }

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        this.windowHandle = glfwCreateWindow(this.width, this.height, this.name, NULL, NULL);
        if(windowHandle == NULL) {
            throw new RuntimeException("Could not create glfw window");
        }

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if(vidMode == null)
            throw new RuntimeException("Could not get glfw vidMode");

        glfwSetWindowPos(windowHandle, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

        glfwSetCursorPosCallback(windowHandle, input::cursorPosCallback);
        glfwSetKeyCallback(windowHandle, input::keyboardCallback);
        glfwSetMouseButtonCallback(windowHandle, input::mouseButtonCallback);

        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwMakeContextCurrent(windowHandle);

        if(vSync) {
            glfwSwapInterval(1);
        } else {
            glfwSwapInterval(0);
        }

        glfwShowWindow(windowHandle);

        GL.createCapabilities();
    }

    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public void cleanup() {
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    public void toggleCursor(Input input) {
        cursorToggle = !cursorToggle;

        if (cursorToggle) {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            input.resetMouse();
        }
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindowHandle() {
        return windowHandle;
    }
}
