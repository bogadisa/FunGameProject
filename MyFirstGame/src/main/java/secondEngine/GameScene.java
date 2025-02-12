package secondEngine;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import secondEngine.renderer.Shader;
import secondEngine.renderer.Texture;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GameScene extends Scene {
    // private float[] vertexArray = {
    // // position // color
    // 50.0f, -0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0
    // -0.0f, 50.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Top left 1
    // 50.0f, 50.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, // Top right 2
    // -0.0f, -0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // Bottom left 3
    // };
    private float[] vertexArray = {
            // position // color // UV coordinates
            50.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1, 1, // Bottom right 0
            0.0f, 50.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0, 0, // Top left 1
            50.0f, 50.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1, 0, // Top right 2
            0.0f, -0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0, 1, // Bottom left 3
            150.0f, 100.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1, 1, // Bottom right 0
            100.0f, 150.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0, 0, // Top left 1
            150.0f, 150.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1, 0, // Top right 2
            100.0f, 100.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0, 1,// Bottom left 3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            /*
             * x x
             * 
             * 
             * x x
             */
            2, 1, 0, // Top right triangle
            0, 1, 3, // bottom left triangle
            6, 5, 4, // Top right triangle
            4, 5, 7 // bottom left triangle
    };

    // VAO: Vertex Array Objects, VBO: Vertex Buffer Objects
    private int vaoID, vboID, eboID;

    private Shader defaultShader;

    public Camera camera;

    public Texture testTexture;

    public GameScene() {

    }

    public void init() {
        this.camera = new Camera(new Vector2f(-480, -270));
        this.defaultShader = new Shader("../../shaders/default.glsl");
        this.defaultShader.compileAndLink();
        this.testTexture = new Texture("resources/icons/heart.png");
        // ============================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ============================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indecies and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

        // Layout location 0
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0); // the index refers to location, as specified in shader

        // Layout location 1
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Layout location 2
        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);

        // makes alpha blend with background
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    }

    public void update() {
        camera.position.x -= 0.4 * Time.getDelta() * 3;
        camera.position.y -= 0.20 * Time.getDelta() * 3;
        // Bind shader program
        defaultShader.use();

        // Upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        // dont need?
        // glEnableVertexAttribArray(0);
        // glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        // dont need?
        // glDisableVertexAttribArray(0);
        // glDisableVertexAttribArray(1);

        testTexture.unbind();
        glBindVertexArray(0);

        defaultShader.detatch();

    }
}
