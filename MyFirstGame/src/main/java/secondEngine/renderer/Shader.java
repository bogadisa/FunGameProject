package secondEngine.renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import secondEngine.renderer.Shader;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import Utils.FileUtils;

public class Shader {
    private int shaderProgramId;

    private String vertexSrc = "", fragmentSrc = "";

    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            InputStream is = getClass().getResourceAsStream(filepath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
    
            String line = br.readLine();
            String type = line.split(" ")[1];

            while ((line = br.readLine()) != null) {
                if (line.startsWith("#type")) {
                    type = line.split(" ")[1];
                } else {
                    if (type.equals("vertex")) {
                        vertexSrc += line + "\n";
                    } else if (type.equals("fragment")) {
                        fragmentSrc += line + "\n";
                    } else {
                        throw new IOException("Unexpected token '" + type + "' in '" + filepath + "'");
                    }
                }
            }
    
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
            assert false : "Error: Could not load shader for file '" + filepath + "'";
        }
    }

    public void compileAndLink() {
        // ============================================================
        // Compile and link shaders
        // ============================================================
        int vertexID, fragmentID;
        // First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH); // needed because gl uses C
            System.out.println("ERROR: '"+ filepath +"'\'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // First load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH); // needed because gl uses C
            System.out.println("ERROR: '"+ filepath +"'\'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }
        

        // Link shaders and check for errors
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexID);
        glAttachShader(shaderProgramId, fragmentID);
        glLinkProgram(shaderProgramId);

        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH); // needed because gl uses C
            System.out.println("ERROR: '"+ filepath +"'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramId, len));
            assert false : "";
        }
    }

    public void use() {
        glUseProgram(shaderProgramId);

    }

    public void detatch() {
        glUseProgram(0);

    }
}
