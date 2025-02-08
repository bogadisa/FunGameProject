package secondEngine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import Main.Time;

public class Window {
    int width, height;

    String title;

    long glfwWindow; // memory address

    static Window window = null;

    private float r, g, b, a;

    public Time time;

    static Shaders shader;

    private Window() {
        this.width = 1920;
        this.height = 1080;

        r = 0;
        g = 0;
        b = 0;
        a = 0;

        this.title = "Fun game!";
        this.time = new Time(24);
    }

    static public Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
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

        shader = new Shaders();
        shader.init();
    }

    public void loop() {
        time.startLoop();
        int width[] = new int[1];
        int height[] = new int[1];
        // double x, y;
        float rgba[] = { 0.0f, 0.0f, 0.0f, 0.0f };
        int i = 0;
        int j = 1;
        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // glfwGetWindowSize(glfwWindow, width, height);
            // rgba[i] = MouseListener.getX() / width[0];
            // rgba[j] = MouseListener.getY() / height[0];

            // x = (MouseListener.getX() - 0.5 * width[0]) / width[0];
            // y = (MouseListener.getY() - 0.5 * height[0]) / height[0];
            // if (x < 0) {
            // r = (float) -x;
            // } else {
            // g = (float) x;
            // }
            // if (y < 0) {
            // b = (float) -y;
            // } else {
            // a = (float) y;
            // }

            // glClearColor(rgba[0], rgba[1], rgba[2], rgba[3]);
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            if (time.readyToDraw()) {
                // update();
                // repaint();
                shader.update();
                time.update();
            }
            shader.update();
            glfwSwapBuffers(glfwWindow);

            // time.increment();

        }
    }
}
