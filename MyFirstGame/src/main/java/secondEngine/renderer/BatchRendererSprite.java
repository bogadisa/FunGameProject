package secondEngine.renderer;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.Transform;
import secondEngine.util.AssetPool;

public class BatchRendererSprite extends BatchRenderer implements Comparable<BatchRendererSprite> {
    private final static int TEX_ID_SIZE = 1;

    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private List<Integer> openSpriteSlots;
    private boolean hasRoom;
    private int zIndex;

    private int[] texSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };

    private List<Texture> textures;

    private boolean manualRebuffer = false;

    public BatchRendererSprite(int maxBatchSize, int zIndex) {
        this(maxBatchSize, zIndex, AssetPool.getShader("/shaders/sprites/default.glsl"));
    }

    public BatchRendererSprite(int maxBatchSize, int zIndex, Shader shader) {
        super(maxBatchSize, shader, POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE);
        this.sprites = new SpriteRenderer[maxBatchSize];

        this.openSpriteSlots = new ArrayList<>();
        this.hasRoom = true;
        this.zIndex = zIndex;
        this.textures = new ArrayList<>();

        addAttribute(TEX_ID_SIZE, TEX_ID_OFFSET);
    }

    public void render() {
        boolean rebufferData = false;
        if (manualRebuffer) {
            rebufferData = true;
            manualRebuffer = false;
        } else {
            for (SpriteRenderer sprite : sprites) {
                if (sprite != null && sprite.isDirty()) {
                    loadVertexProperties(sprite.getBatchIndex());
                    sprite.setClean();
                    rebufferData = true;
                }
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
        for (int i = 0; i < textures.size(); i++) {
            // sets the first element to no texture
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        draw();

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detatch();

    }

    public void addSprite(SpriteRenderer spr) {
        // TODO add overflow sprites to another batch renderer
        if (hasRoom) {
            int index;
            if (this.openSpriteSlots.size() > 0) {
                index = this.openSpriteSlots.remove(this.openSpriteSlots.size() - 1);
            } else {
                index = this.numObjects;
            }
            this.sprites[index] = spr;
            this.numObjects++;

            if (spr.getTexture() != null) {
                if (!textures.contains(spr.getTexture())) {
                    textures.add(spr.getTexture());
                }
            }

            // Add properties to local vertices array
            loadVertexProperties(index);

            if (numObjects >= this.maxBatchSize) {
                hasRoom = false;
            }
            spr.setAddedToRenderer(true, this, index);
        }
    }

    public void removeSprite(SpriteRenderer spr) {
        int index = spr.getBatchIndex();
        this.sprites[index] = null;
        resetVertexProperties(index);
        spr.setAddedToRenderer(false, null);
        this.openSpriteSlots.add(index);
        this.numObjects--;

        this.manualRebuffer = true;
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
            for (int i = 0; i < textures.size(); i++) {
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
            transformMatrix.rotate((float) Math.toRadians(transform.rotation), 0, 0, 1);
        }

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

            // May be a source of z trouble
            Vector4f currentPos = new Vector4f(transform.position.x + (xAdd * transform.scale.x),
                    transform.position.y + (yAdd * transform.scale.y), transform.position.z, 1);
            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 1, 1).mul(transformMatrix);
            }

            // for (int j = 0; j < 9; j++) {
            // vertices[offset + j] = 1;
            // }
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

    private void resetVertexProperties(int index) {
        int offset = index * 4 * VERTEX_SIZE;
        for (int i = 0; i < 4; i++) {

            // Load position
            vertices[offset] = 0.0f;
            vertices[offset + 1] = 0.0f;
            vertices[offset + 2] = 0.0f;

            // Load color
            vertices[offset + 3] = 0.0f;
            vertices[offset + 4] = 0.0f;
            vertices[offset + 5] = 0.0f;
            vertices[offset + 6] = 0.0f;

            // Load texture coordinates
            vertices[offset + 7] = 0.0f;
            vertices[offset + 8] = 0.0f;

            // Load texture id
            vertices[offset + 9] = 0.0f;
            offset += VERTEX_SIZE;
        }
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
    public int compareTo(BatchRendererSprite o) {
        return Integer.compare(this.zIndex, o.getzIndex());
    }
}
