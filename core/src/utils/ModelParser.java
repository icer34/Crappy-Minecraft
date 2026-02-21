package utils;

import graphics.MeshData;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ModelParser {
    private ModelParser(){}

    private record Vertex (float[] pos, float[] norm, float[] uv, int idx) {}

    public static MeshData parseOBJ(String path) {
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
        ArrayList<String> faceData = new ArrayList<>();

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

                case "f" -> {
                    faceData.add(parts[1]);
                    faceData.add(parts[2]);
                    faceData.add(parts[3]);
                }

                default -> {
                }
            }
        }

        //from the face data, keep only once the vertices needed and then add to the final data
        //map that keeps the known vertices, key = the vertex in the obj format (pos/tex/norm), val = Vertex(pos, norm, uv)
        HashMap<String, Vertex> knownVertices = new HashMap<>();
        FloatArrayList finalVertData = new FloatArrayList();
        IntArrayList finalIdxData = new IntArrayList();
        int numVertices = 0;

        for(String vert : faceData) {
            if(knownVertices.containsKey(vert)) {
                Vertex v = knownVertices.get(vert);
                finalIdxData.add(v.idx);
            } else {
                String[] vertexIdx = vert.split("/");
                Vertex v = new Vertex(positions.get(Integer.parseInt(vertexIdx[0]) - 1),
                                      normals.get(Integer.parseInt(vertexIdx[2]) - 1),
                                      uvs.get(Integer.parseInt(vertexIdx[1]) - 1),
                                      numVertices++);

                knownVertices.put(vert, v);

                finalVertData.add(v.pos[0]); finalVertData.add(v.pos[1]); finalVertData.add(v.pos[2]);
                finalVertData.add(v.norm[0]); finalVertData.add(v.norm[1]); finalVertData.add(v.norm[2]);
                finalVertData.add(v.uv[0]); finalVertData.add(v.uv[1]);

                finalIdxData.add(v.idx);
            }
        }

        return new MeshData(finalVertData.toFloatArray(), finalIdxData.toIntArray());
    }
}
