package a3;

import tage.*;
import tage.shapes.*;
public class MyObject extends ManualObject {
    private float[] vertices = new float[] {
        -1.0f, 0f,  1.0f,    1.0f, 0f,  1.0f,   0.0f, 1.5f, 0.0f,
         1.0f, 0f,  1.0f,    1.0f, 0f, -1.0f,   0.0f, 1.5f, 0.0f,
         1.0f, 0f, -1.0f,   -1.0f, 0f, -1.0f,   0.0f, 1.5f, 0.0f,
        -1.0f, 0f, -1.0f,   -1.0f, 0f,  1.0f,   0.0f, 1.5f, 0.0f,
         1.0f, 0f, -1.0f,   -1.0f, 0f, -1.0f,   0.0f, -1.5f, 0.0f,
        -1.0f, 0f, -1.0f,   -1.0f, 0f,  1.0f,   0.0f, -1.5f, 0.0f,
        -1.0f, 0f,  1.0f,    1.0f, 0f,  1.0f,   0.0f, -1.5f, 0.0f,
         1.0f, 0f,  1.0f,    1.0f, 0f, -1.0f,   0.0f, -1.5f, 0.0f };

    private float[] texcoords = new float[] {
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f,
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f,
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f,
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f,
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f,
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f,
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f,
        0.0f, 0.0f,   1.0f, 0.0f,   0.5f, 1.0f };

    private float[] normals = new float[] {
        0.0f, 1.0f,  1.0f,    0.0f, 1.0f,  1.0f,    0.0f, 1.0f,  1.0f,
        1.0f, 1.0f,  0.0f,    1.0f, 1.0f,  0.0f,    1.0f, 1.0f,  0.0f,
        0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,
        -1.0f, 1.0f,  0.0f,   -1.0f, 1.0f,  0.0f,   -1.0f, 1.0f,  0.0f,
        0.0f, 1.0f,  1.0f,    0.0f, 1.0f,  1.0f,    0.0f, 1.0f,  1.0f,
        1.0f, 1.0f,  0.0f,    1.0f, 1.0f,  0.0f,    1.0f, 1.0f,  0.0f,
        0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,    0.0f, 1.0f, -1.0f,
        -1.0f, 1.0f,  0.0f,   -1.0f, 1.0f,  0.0f,   -1.0f, 1.0f,  0.0f };

    public MyObject() {
        super();
        setNumVertices(24);
        setVertices(vertices);
        setTexCoords(texcoords);
        setNormals(normals);
        setMatAmb(Utils.goldAmbient());
        setMatDif(Utils.goldDiffuse());
        setMatSpe(Utils.goldSpecular());
        setMatShi(Utils.goldShininess());
    }
}
