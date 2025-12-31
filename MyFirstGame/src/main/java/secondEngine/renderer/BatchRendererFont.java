package secondEngine.renderer;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.components.TextRenderer;
import secondEngine.components.Transform;
import secondEngine.components.helpers.TextBox.CharacterInfo;
import secondEngine.util.AssetPool;

public class BatchRendererFont extends BatchRenderer {

    private Font font;
    private List<TextRenderer> texts;

    public BatchRendererFont(int maxBatchSize) {
        this(maxBatchSize, AssetPool.getFont("resources/fonts/Unifontexmono-lxY45.ttf"));
    }

    public BatchRendererFont(int maxBatchSize, Font font) {
        this(maxBatchSize, font, AssetPool.getShader("/shaders/fonts/default.glsl"));
    }

    public BatchRendererFont(int maxBatchSize, Font font, Shader shader) {
        super(maxBatchSize, shader);

        this.font = font;
        this.texts = new ArrayList<>();
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

        draw();

        font.unbind();
        shader.detatch();

        // Reset batch for use on next draw call
        this.numObjects = 0;
    }

    public void addChar(CharacterInfo c, Transform transform, Vector4f color) {

        if (this.numObjects >= (this.maxBatchSize - 1)) {
            flushBatch();
        }
        // Find offset within array (4 vertices per char)
        int offset = 4 * this.numObjects * VERTEX_SIZE;

        Vector3f charPos = new Vector3f(transform.position).add(c.pos);
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
            Vector3f currentPos = new Vector3f(charPos.x + (xAdd * c.scale.x), charPos.y + (yAdd * c.scale.y),
                    charPos.z);

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
        this.numObjects += 1;
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
        text.setAddedToRenderer(true, this);
    }

    public void removeText(TextRenderer text) {
        texts.remove(text);
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
