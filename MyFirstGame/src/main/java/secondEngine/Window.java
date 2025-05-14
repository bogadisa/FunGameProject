package secondEngine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;

import secondEngine.Config.CameraConfig;
import secondEngine.listeners.KeyListener;
import secondEngine.listeners.MouseListener;
import secondEngine.scenes.*;
import secondEngine.util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;

public class Window {
    int width, height;

    String title;

    static long glfwWindow; // memory address

    static Window window = null;

    private float r, g, b, a;

    private static Scene currentScene = null;

    private Window() {
        // initializes the configs
        Config.getCameraConfig();
        Config.getUIconfig();

        this.width = CameraConfig.resWidth;
        this.height = CameraConfig.resHeight;

        r = 1;
        g = 1;
        b = 1;
        a = 1;

        this.title = "Fun game!";
    }

    static public Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new EditorScene();
                currentScene.load();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new GameScene();
                currentScene.load();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }
    }
    
    public static Scene getScene() {
        return Window.currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        glfwSetWindowSizeCallback(glfwWindow, Window::windowResizeCallback);
        glfwSetWindowPosCallback(glfwWindow, Window::windowReposCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        IntBuffer imgWidth = BufferUtils.createIntBuffer(1);
        IntBuffer imgHeight = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(glfwWindow, imgWidth, imgHeight);
        CameraConfig.width = imgWidth.get(0); 
        CameraConfig.height = imgHeight.get(0);

        Window.changeScene(1);
    }

    public void loop() {
        // Resets the loop
        Time.startLoop();
        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();
            if (Time.readyToDraw()) {
                this.render();
            }

            Time.increment();

        }
        Window.getScene().save();
    }

    public void render() {
        glClearColor(r, g, b, a);
        glClear(GL_COLOR_BUFFER_BIT);
        
        currentScene.update();
        Time.update();
        glfwSwapBuffers(glfwWindow);
    }

    public static void setCursor(String filepath) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
        
        // TODO Fix loading vertically
        // stbi_set_flip_vertically_on_load(false);
        GLFWImage img = GLFWImage.malloc().set(width.get(0), height.get(0), image);
        long cursor = glfwCreateCursor(img, 0, 0);
        glfwSetCursor(Window.glfwWindow, cursor);

        // stbi_set_flip_vertically_on_load(true);

        img.free();
        stbi_image_free(image);

    }

    private static void windowResizeCallback(long window, int width, int height) {
        CameraConfig.width = width;
        CameraConfig.height = height;

        glViewport(0, 0, width, height);
        Window.currentScene.camera().adjustProjection();

        if (Time.readyToDraw()) {
            get().render();
        }
        Window.currentScene.resize();

        Time.increment();
    }

    private static void windowReposCallback(long window, int xpos, int ypos) {
        MouseListener.setWindowPos(new Vector2f(xpos, ypos));
    }
}
