package secondEngine.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.components.TextRenderer;
import secondEngine.components.Transform;
import secondEngine.components.helpers.TextBox.CharacterInfo;
import secondEngine.util.AssetPool;
import secondEngine.util.Time;

public class BatchRendererFont {
    private final int POS_SIZE = 3;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;

    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE;

    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    private float[] vertices;

    private int maxCharSize;
    private int charsAdded;
    private int maxSize;
    private int size;

    private int vaoID, vboID;
    private Shader shader;

    private Font font;
    private List<TextRenderer> texts;

    public BatchRendererFont(int maxCharSize) {
        this(maxCharSize, AssetPool.getFont("resources/fonts/Unifontexmono-lxY45.ttf"));
    }

    public BatchRendererFont(int maxCharSize, Font font) {
        this(maxCharSize, font, AssetPool.getShader("/shaders/fonts/default.glsl"));
    }

    public BatchRendererFont(int maxCharSize, Font font, Shader shader) {
        this.maxCharSize = maxCharSize;
        // 4 vertices quads
        this.maxSize = 4 * maxCharSize;
        this.shader = shader;

        this.vertices = new float[this.maxSize * VERTEX_SIZE];

        this.font = font;
        this.texts = new ArrayList<>();
        this.charsAdded = 0;
        this.size = 0;
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
    }

    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxCharSize];
        for (int i = 0; i < maxCharSize; i++) {
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

    public void flushBatch() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Bind shader program
        shader.use();

        glActiveTexture(GL_TEXTURE0);
        font.bind();

        shader.uploadTexture("uFontTexture", 0);
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        glBindVertexArray(vaoID);

        // glEnableVertexAttribArray(0);
        // glEnableVertexAttribArray(1);
        // glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, this.charsAdded * 6, GL_UNSIGNED_INT, 0);

        // glDisableVertexAttribArray(0);
        // glDisableVertexAttribArray(1);
        // glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        font.unbind();
        shader.detatch();

        // Reset batch for use on next draw call
        this.charsAdded = 0;
        this.size = 0;
    }

    public void addChar(CharacterInfo c, Transform transform, Vector4f color) {

        if (this.charsAdded >= (this.maxCharSize - 1)) {
            flushBatch();
        }
        // Find offset within array (4 vertices per char)
        int offset = this.size * VERTEX_SIZE;

        Vector2f charPos = transform.position.xy(new Vector2f()).add(c.pos);
        Vector2f[] texCoords = c.metrics.texCoords;

        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }
            Vector4f currentPos = new Vector4f(charPos.x + (xAdd * c.scale.x), charPos.y + (yAdd * c.scale.y), 0, 1);

            // Load position
            // for (int j = 0; j < 9; j++) {
            // vertices[offset + j] = 1;
            // }
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

            offset += VERTEX_SIZE;
        }
        this.charsAdded += 1;
        // 4 verticies
        this.size += 4;
    }

    public void loadVertexProperties(TextRenderer text) {
        Transform transform = text.gameObject.transform;
        Vector4f color = text.getColor();

        for (CharacterInfo c : text.getCharacters()) {
            addChar(c, transform, color);
        }
    }

    public void addText(TextRenderer text) {
        texts.add(text);
        // if (!text.getCharacters().isEmpty()) {
        // loadVertexProperties(text);
        // }
        text.setAddedToRenderer(true, this);
    }

    public void render() {
        for (TextRenderer text : texts) {
            if (text != null) {
                loadVertexProperties(text);
            }
        }
        flushBatch();
    }
}
