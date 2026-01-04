package graphics;

import org.joml.Vector3f;

public class DirectionalLight {

    private final Vector3f dir = new Vector3f(0, -1, 0);
    private final Vector3f color = new Vector3f(1, 1, 1);
    private float angle = (float) Math.PI / 2;

    public DirectionalLight(){}

    public void update(float dt, float speed) {
        angle += speed * dt;

        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);

        dir.set(0.2f, -s, 0.3f + c).normalize();
    }

    public Vector3f getDir() { return dir; }
    public Vector3f getColor() { return color; }
}
