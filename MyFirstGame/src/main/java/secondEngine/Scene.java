package secondEngine;

import java.util.ArrayList;
import java.util.List;

import secondEngine.renderer.Renderer;

public abstract class Scene {
    protected Renderer renderer = new Renderer();

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected Camera camera = null;
    protected float[] vertexArray;
    protected int[] elementArray;

    private boolean isRunning = false;

    public abstract void init();

    public void start() {
        for (GameObject go: gameObjects) {
            go.start();
            this.renderer.add(go);
        }

        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public abstract void update();

    public Camera camera() {
        return this.camera;
    }

    protected void createVertices(int screenWidth, int screenHeight, int quadWidth, int quadHeight) {
        int nCols = (int) (screenWidth / quadWidth);
        int nRows = (int) (screenHeight / quadHeight);
        vertexArray = new float[nCols * nRows * 4 * 9];
        elementArray = new int[nCols * nRows * 3 * 2];
        int curX = 0;
        int curY = 0;
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                // top right triangle
                elementArray[(i * nCols) + j] = (i * nCols) + j + 2;
                elementArray[(i * nCols) + j + 1] = (i * nCols) + j + 1;
                elementArray[(i * nCols) + j + 2] = (i * nCols) + j;
                // bot left triangle
                elementArray[(i * nCols) + j + 3] = (i * nCols) + j;
                elementArray[(i * nCols) + j + 4] = (i * nCols) + j + 1;
                elementArray[(i * nCols) + j + 5] = (i * nCols) + j + 3;
                // bot left
                // ---- pos ----
                vertexArray[(9 * 4 * i * nCols) + j] = (float) curX;
                vertexArray[(9 * 4 * i * nCols) + j + 1] = (float) curY;
                vertexArray[(9 * 4 * i * nCols) + j + 2] = 0;
                // ---- color ----
                vertexArray[(9 * 4 * i * nCols) + j + 3] = 0;
                vertexArray[(9 * 4 * i * nCols) + j + 4] = 0;
                vertexArray[(9 * 4 * i * nCols) + j + 5] = 0;
                vertexArray[(9 * 4 * i * nCols) + j + 6] = 0;
                // ---- uv ----
                vertexArray[(9 * 4 * i * nCols) + j + 7] = 0;
                vertexArray[(9 * 4 * i * nCols) + j + 8] = 1;
                // bot right
                // ---- pos ----
                vertexArray[(9 * 4 * i * nCols) + 9 * j] = (float) curX + quadWidth;
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 1] = (float) curY;
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 2] = 0;
                // ---- color ----
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 3] = 0;
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 4] = 0;
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 5] = 0;
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 6] = 0;
                // ---- uv ----
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 7] = 1;
                vertexArray[(9 * 4 * i * nCols) + 9 * j + 8] = 1;
                // top left
                // ---- pos ----
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j] = (float) curX;
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 1] = (float) curY + quadHeight;
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 2] = 0;
                // ---- color ----
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 3] = 0;
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 4] = 0;
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 5] = 0;
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 6] = 0;
                // ---- uv ----
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 7] = 0;
                vertexArray[(9 * 4 * i * nCols) + 2 * 9 * j + 8] = 0;
                // top right
                // ---- pos ----
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j] = (float) curX + quadWidth;
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 1] = (float) curY + quadHeight;
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 2] = 0;
                // ---- color ----
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 3] = 0;
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 4] = 0;
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 5] = 0;
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 6] = 0;
                // ---- uv ----
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 7] = 1;
                vertexArray[(9 * 4 * i * nCols) + 3 * 9 * j + 8] = 0;

                curX += quadWidth;
            }
            curX = 0;
            curY += quadHeight;
        }

        // int curX = 0;
        // int curY = 0;
        // int i = 0;
        // int u = 0;
        // int v = 1;
        // while (curY <= screenHeight) {
        // while (curX <= screenWidth) {
        // vertexArray[i] = (float) curX;
        // vertexArray[i + 1] = (float) curY;
        // vertexArray[i + 2] = 0.0f;
        // vertexArray[i + 3] = 0.0f;
        // vertexArray[i + 4] = 0.0f;
        // vertexArray[i + 5] = 0.0f;
        // vertexArray[i + 6] = 0.0f;
        // vertexArray[i + 7] = (float) u;
        // vertexArray[i + 8] = (float) v;

        // curX += quadWidth;
        // i += 9;

        // u++;
        // if (u > 1) {
        // u = 0;
        // }
        // }
        // curX = 0;
        // curY += quadHeight;

        // v++;
        // if (v > 1) {
        // v = 0;
        // }

        // }
    }
}
