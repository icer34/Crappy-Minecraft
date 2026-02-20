package utils;

import graphics.MeshData;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ModelParser {
    private ModelParser(){}

    private record Vertex (float[] pos, float[] norm, float[] uv) {}

    public MeshData parseOBJ(String path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //retrieve all the positions - normals - UVs
        ArrayList<float[]> positions = new ArrayList<>();
        ArrayList<float[]> normals = new ArrayList<>();
        ArrayList<float[]> uvs = new ArrayList<>();

        for (String l : lines) {
            String[] parts = l.split(" ");
            switch (parts[0]) {
                case "v" -> {
                    //vertex position data
                    positions.add(new float[]{Float.parseFloat(parts[1]),
                                              Float.parseFloat(parts[2]),
                                              Float.parseFloat(parts[3])});
                }

                case "vn" -> {
                    //vertex normal
                    normals.add(new float[]{Float.parseFloat(parts[1]),
                                            Float.parseFloat(parts[2]),
                                            Float.parseFloat(parts[3])});
                }

                case "vt" -> {
                    //vertex texture coords
                    uvs.add(new float[]{Float.parseFloat(parts[1]),
                                        Float.parseFloat(parts[2])});
                }

                default -> {
                }
            }
        }

        //from the face data, keep only once the vertices needed and then add to the final data
        //TODO
    }
}
