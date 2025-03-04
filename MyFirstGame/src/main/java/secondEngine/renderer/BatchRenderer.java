package secondEngine.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.components.SpriteRenderer;

public class BatchRenderer {
    // Vertex
    // ======
    // Pos                      Color                          UV
    // float, float, float,     float, float, float, float,    float, float
    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int UV_SIZE = 2;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int UV_OFFSET = COLOR_OFFSET + (COLOR_SIZE + POS_SIZE) * Float.BYTES;
    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + UV_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    
    private SpriteRenderer[] sprites;
    private int numSprites;

    private boolean hasRoom;
    private float[] vertices;
    
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public BatchRenderer(int maxBatchSize) {
        shader = new Shader("assets/shaders/default.glsl");
        shader.compileAndLink();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        
        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
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

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, UV_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, UV_OFFSET);
        glEnableVertexAttribArray(2);

    }

    public void render() {
        // FOR now, we will rebuffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Bind shader program
        shader.use();

        // Upload texture to shader
        shader.uploadMat4f("uProjection", Window.getScene().camera.getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera.getViewMatrix());
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        // dont need?
        // glEnableVertexAttribArray(0);
        // glEnableVertexAttribArray(1);
        // glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
        // Unbind everything
        // dont need?
        // glDisableVertexAttribArray(0);
        // glDisableVertexAttribArray(1);
        // glDisableVertexAttribArray(2);

        glBindVertexArray(0);
        shader.detatch();

        
    }

    public void addSprite(SpriteRenderer spr) {
        if (hasRoom) {
            int index = this.numSprites;
            this.sprites[index] = spr;
            this.numSprites++;
    
            // Add properties to local vertices array
            loadVertexProperties(index);
    
            if (numSprites >= this.maxBatchSize) {
                hasRoom = false;
            }

        }
    }

    public void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = sprite.getColor();

        
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        // 1,1, 1,0, 0,0, 0,1
        for (int i=0; i < 4; i++){
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }
            vertices[offset] = sprite.gameObject.transform.position.x + xAdd*sprite.gameObject.transform.scale.x;
            vertices[offset + 1] = sprite.gameObject.transform.position.y + yAdd*sprite.gameObject.transform.scale.y;
            vertices[offset + 2] = sprite.gameObject.transform.position.z;

            vertices[offset + 3] = color.x;
            vertices[offset + 4] = color.y;
            vertices[offset + 5] = color.z;
            vertices[offset + 6] = color.w;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }
}
