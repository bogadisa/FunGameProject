package secondEngine.scenes;

import java.util.ArrayList;
import java.util.List;

import secondEngine.Camera;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;
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
    }
    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);

        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}
