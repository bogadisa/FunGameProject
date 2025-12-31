package secondEngine.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.List;

public abstract class BatchRenderer {
    private class Attribute {
        private int size, offset;

        private Attribute(int size, int offset) {
            this.size = size;
            this.offset = offset;
        }
    }

    protected final static int POS_SIZE = 3;
    protected final static int COLOR_SIZE = 4;
    protected final static int TEX_COORDS_SIZE = 2;

    protected final static int POS_OFFSET = 0;
    protected final static int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    protected final static int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    protected final int VERTEX_SIZE;

    protected final int VERTEX_SIZE_BYTES;

    protected int numObjects;
    protected float[] vertices;

    protected int vaoID, vboID;
    protected int maxBatchSize;
    protected Shader shader;

    private List<Attribute> attributes;

    public BatchRenderer(int maxBatchSize, Shader shader) {
        this(maxBatchSize, shader, POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE);
    }

    public BatchRenderer(int maxBatchSize, Shader shader, int vertex_size) {
        this.VERTEX_SIZE = vertex_size;
        this.VERTEX_SIZE_BYTES = this.VERTEX_SIZE * Float.BYTES;

        this.shader = shader;
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numObjects = 0;
        this.attributes = new ArrayList<>();
        attributes.add(new Attribute(POS_SIZE, POS_OFFSET));
        attributes.add(new Attribute(COLOR_SIZE, COLOR_OFFSET));
        attributes.add(new Attribute(TEX_COORDS_SIZE, TEX_COORDS_OFFSET));
    }

    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        for (int i = 0; i < attributes.size(); i++) {
            Attribute atr = attributes.get(i);
            glVertexAttribPointer(i, atr.size, GL_FLOAT, false, VERTEX_SIZE_BYTES, atr.offset);
            glEnableVertexAttribArray(i);
        }

    }

    protected void addAttribute(int size, int offset) {
        attributes.add(new Attribute(size, offset));
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1 7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public abstract void render();

    public void draw() {
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);
        for (int i = 0; i < attributes.size(); i++) {
            glEnableVertexAttribArray(i);
        }
        glDrawElements(GL_TRIANGLES, this.numObjects * 6, GL_UNSIGNED_INT, 0);
        for (int i = 0; i < attributes.size(); i++) {
            glDisableVertexAttribArray(i);
        }
        // Unbind the VAO that we're using
        glBindVertexArray(0);
    }
}
