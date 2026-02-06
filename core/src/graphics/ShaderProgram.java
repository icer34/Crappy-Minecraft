package graphics;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {
    private final String name;

    private int programID;

    private int vertID;
    private int fragID;
    private int geomID;

    private Map<String, Integer> uniforms;

    public ShaderProgram(String name ) {
        this.name = name;
        programID = glCreateProgram();
        uniforms = new HashMap<>();
    }

    public void addShader(String path, int type) {
        int id = glCreateShader(type);
        switch (type) {
            case GL_FRAGMENT_SHADER:
                fragID = id;
                break;

            case GL_VERTEX_SHADER:
                vertID = id;
                break;

            case GL_GEOMETRY_SHADER:
                geomID = id;
                break;

            default:
                throw new RuntimeException("Please select a valid shader type");
        }

        String shaderCode;
        try {
            shaderCode = Files.readString(Path.of(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        glShaderSource(id, shaderCode);
        glCompileShader(id);

        if(glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Could not compile shader: " + glGetShaderInfoLog(id));
        }

        glAttachShader(programID, id);
    }

    public void createUniforms(String[] names) {
        for(String name : names) {
            createUniform(name);
        }
    }

    public void setUniform(String name, Object value) {
        switch (value) {
            case null -> throw new IllegalArgumentException("Can't set null uniform: " + name);
            case Integer i -> setUniform(name, i.intValue());
            case Float f -> setUniform(name, f.floatValue());
            case Matrix4f m -> setUniform(name, m);
            case Vector2f v2 -> setUniform(name, v2);
            case Vector3f v3 -> setUniform(name, v3);
            default -> throw new IllegalArgumentException(
                    "Unsupported uniform type for '" + name + "': " + value.getClass()
            );
        }
    }

    public void setUniforms(String[] names, Object[] values) {
        if(names.length != values.length)
            throw new IllegalArgumentException("Size missmatch in uniform initialization for program: " + name);

        bind();

        for(int i = 0; i < names.length; i++) {
            setUniform(names[i], values[i]);
        }
    }

    private void createUniform(String name) {
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
            String log = glGetProgramInfoLog(programID);
            throw new IllegalStateException("Program not linked or linking failed. InfoLog: " + log);
        }

        int loc = glGetUniformLocation(programID, name);
        if(loc < 0)
            throw new RuntimeException("Could not find uniform: " + name);
        uniforms.put(name, loc);
    }

    private void setUniform(String name, float value) {
        Integer loc = uniforms.get(name);
        if (loc == null) {
            throw new IllegalStateException(
                    "Uniform '" + name + "' not created or not found in shader"
            );
        }
        glUniform1f(loc, value);
    }

    private void setUniform(String name, int value) {
        Integer loc = uniforms.get(name);
        if (loc == null) {
            throw new IllegalStateException(
                    "Uniform '" + name + "' not created or not found in shader"
            );
        }
        glUniform1i(loc, value);
    }

    private void setUniform(String name, Vector3f value) {
        Integer loc = uniforms.get(name);
        if (loc == null) {
            throw new IllegalStateException(
                    "Uniform '" + name + "' not created or not found in shader"
            );
        }
        glUniform3f(loc, value.x, value.y, value.z);
    }

    private void setUniform(String name, Vector2f value) {
        Integer loc = uniforms.get(name);
        if (loc == null) {
            throw new IllegalStateException(
                    "Uniform '" + name + "' not created or not found in shader"
            );
        }
        glUniform2f(loc, value.x, value.y);
    }

    private void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(name), false, fb);
        }
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void link() {
        glLinkProgram(programID);
    }

    public void cleanup() {
        unbind();
        if(programID != 0)
            glDeleteProgram(programID);
    }

    public String getName() {
        return name;
    }
}
