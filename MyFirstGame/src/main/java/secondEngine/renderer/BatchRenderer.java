package secondEngine.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.Transform;
import secondEngine.util.AssetPool;

public class BatchRenderer implements Comparable<BatchRenderer>{
    // Vertex
    // ======
    // Pos                      Color                       tex coords      tex id
    // float, float, float      float, float, float, float  float, float    float
    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE;
    
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    
    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private int zIndex;

    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public BatchRenderer(int maxBatchSize, int zIndex) {
        shader = AssetPool.getShader("/shaders/default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        
        // 4 vertices quads
        this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        // this.previousVert = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.zIndex = zIndex;
        this.textures = new ArrayList<>();
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

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void render() {
        boolean rebufferData = false;
        for (int i=0; i < numSprites; i++) {
            SpriteRenderer sprite = sprites[i];
            if (sprite.isDirty()) {
                loadVertexProperties(i);
                sprite.setClean();
                rebufferData = true;
            }
        }
        if (rebufferData) {
            // FOR now, we will rebuffer all data every time something is dirty
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Bind shader program
        shader.use();

        // Upload texture to shader
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        for (int i=0; i < textures.size(); i++) {
            // sets the first element to no texture
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        // dont need?
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        // glEnableVertexAttribArray(2);
        // glEnableVertexAttribArray(3);
        // glEnableVertexAttribArray(4);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
        // Unbind everything
        // dont need?
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        // glDisableVertexAttribArray(2);
        // glDisableVertexAttribArray(3);
        // glDisableVertexAttribArray(4);

        glBindVertexArray(0);

        for (int i=0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detatch();

        
    }

    public void addSprite(SpriteRenderer spr) {
        // TODO add overflow sprites to another batch renderer
        if (hasRoom) {
            int index = this.numSprites;
            this.sprites[index] = spr;
            this.numSprites++;

            if (spr.getTexture() != null) {
                if (!textures.contains(spr.getTexture())) {
                    textures.add(spr.getTexture());
                }
            }
    
            // Add properties to local vertices array
            loadVertexProperties(index);
    
            if (numSprites >= this.maxBatchSize) {
                hasRoom = false;
            }
            spr.setAddedToRenderer(true);
        }
    }

    public void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];
        Transform transform = sprite.getTransform();

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texId = 0;
        if (sprite.getTexture() != null) {
            for (int i=0; i < textures.size(); i++) {
                if (textures.get(i) == sprite.getTexture()) {
                    // sets the first element to no texture
                    if (sprite.isHidden()) {
                        break;
                    }
                    texId = i + 1;
                    break;
                }
            }
        } 

        boolean isRotated = transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            // scale (flipping) must happen after rotating 
            // meaning further left in the equation
            // meaning applied to the matrix before rotate
            transformMatrix.translate(transform.position);
            transformMatrix.scale(transform.scale);
            transformMatrix.rotate((float)Math.toRadians(transform.rotation),
                    0, 0, 1);
        }

        
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            // May be a source of z trouble
            Vector4f currentPos = new Vector4f(transform.position.x + (xAdd * transform.scale.x),
                    transform.position.y + (yAdd * transform.scale.y),
                    transform.position.z, 1);
            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 1, 1).mul(transformMatrix);
            }

            // Load position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;
            vertices[offset + 2] = currentPos.z;

            // Load color
            vertices[offset + 3] = color.x;
            vertices[offset + 4] = color.y;
            vertices[offset + 5] = color.z;
            vertices[offset + 6] = color.w;

            // Load texture coordinates
            vertices[offset + 7] = texCoords[i].x;
            vertices[offset + 8] = texCoords[i].y;

            // Load texture id
            vertices[offset + 9] = texId;
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
    
    public int getzIndex() {
        return this.zIndex;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < this.texSlots.length;
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    @Override
    public int compareTo(BatchRenderer o) {
        return Integer.compare(this.zIndex, o.getzIndex());
    }
}
