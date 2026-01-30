package utils;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private boolean[] keysPressed = new boolean[GLFW_KEY_LAST + 1];

    private boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private boolean[] buttonsPressed = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    private double mouseX, mouseY;
    private double dx, dy;
    private boolean firstMouse = true;

    private boolean enabled = true;

    public void keyboardCallback(long window, int key, int scancode, int action, int mods) {
        if (key < 0 || key >= keys.length) return;

        if (action == GLFW_PRESS) {
            keys[key] = true;
            keysPressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            keys[key] = false;
        }
    }


    public void mouseButtonCallback(long window, int button, int action, int mods) {
        if(!enabled) return;

        if (button < 0 || button >= buttons.length) return;

        if(action == GLFW_PRESS) {
            buttons[button] = true;
            buttonsPressed[button] = true;
        } else if(action == GLFW_RELEASE) {
            buttons[button] = false;
        }
    }

    public void cursorPosCallback(long window, double xPos, double yPos) {
        if(firstMouse) {
            dx = 0;
            dy = 0;
            mouseX = xPos;
            mouseY = yPos;
            firstMouse = false;
            return;
        }

        dx += xPos - mouseX;
        dy += yPos - mouseY;
        mouseX = xPos;
        mouseY = yPos;
    }

    public boolean isKeyPressed(int key) {
        return keys[key];
    }

    public boolean consumeKeyPress(int key) {
        boolean val = keysPressed[key];
        keysPressed[key] = false;
        return val;
    }

    public boolean isMouseButtonPressed(int button) {
        return buttons[button];
    }

    public boolean consumeButtonPress(int button) {
        boolean val = buttonsPressed[button];
        buttonsPressed[button] = false;
        return val;
    }

    public double consumeDx() {
        double tmp = dx;
        dx = 0;
        return tmp;
    }

    public double consumeDy() {
        double tmp = dy;
        dy = 0;
        return tmp;
    }

    public void resetMouse() {
        firstMouse = true;
        dx = 0;
        dy = 0;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
        Arrays.fill(keysPressed, false);
        Arrays.fill(buttonsPressed, false);
    }
}
