package secondEngine.scenes;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import secondEngine.Camera;
import secondEngine.Component;
import secondEngine.SpatialGrid;
import secondEngine.Window;
import secondEngine.components.Overlay;
import secondEngine.components.Transform;
import secondEngine.data.UpdateHierarchy.Priority;
import secondEngine.objects.GameObject;
import secondEngine.renderer.Renderer;
import secondEngine.util.ComponentDeserializer;
import secondEngine.util.GameObjectDeserializer;
import secondEngine.util.Time;

public abstract class Scene {
    protected Renderer renderer = new Renderer();

    protected List<GameObject> gameObjects = new ArrayList<>();
    protected boolean newGameObjectsQueued = false;
    private List<GameObject> gameObjectToAdd = new ArrayList<>();

    protected SpatialGrid worldGrid = null;
    protected SpatialGrid overlayGrid = null;

    protected Camera camera = null;
    protected float[] vertexArray;
    protected int[] elementArray;

    private boolean isRunning = false;

    private boolean sceneIsLoaded = false;

    public abstract void init();

    public void start() {
        for (Priority prio : Priority.values()) {
            for (GameObject go : gameObjects) {
                go.start(prio);
                this.renderer.add(go);
            }

        }

        isRunning = true;
    }

    public void update() {
        float dt = (float) Time.getDelta();
        for (Priority prio : Priority.values()) {
            for (GameObject go : this.gameObjects) {
                go.update(dt, prio);
            }

        }

        if (this.newGameObjectsQueued) {
            this.startQueuedGameObjects();
        }
        this.renderer.render();
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjectToAdd.add(go);
            newGameObjectsQueued = true;
        }
    }

    protected void startQueuedGameObjects() {
        for (Priority prio : Priority.values()) {
            for (GameObject go : gameObjectToAdd) {
                gameObjects.add(go);
                go.start(prio);
                this.renderer.add(go);
            }

        }
        gameObjectToAdd.clear();
        newGameObjectsQueued = false;
    }

    public Camera camera() {
        return this.camera;
    }

    public SpatialGrid worldGrid() {
        return this.worldGrid;
    }

    public GameObject createGameObject(String name) {
        return createGameObject(name, new Transform());
    }

    public GameObject createGameObject(String name, Transform transform) {
        GameObject go = new GameObject(name, Window.incrementAndGetCurrentObjectId());

        go.addComponent(transform);
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void removeGameObject(GameObject goToRemove) {
        gameObjects.remove(goToRemove);

    }

    public void removeGameObject(String name) {
        int i = 0;
        for (GameObject go : gameObjects) {
            if (name.equals(go.getName())) {
                gameObjects.remove(i);
                break;
            }
            i++;

        }

    }

    public void resize() {
        for (GameObject go : gameObjects) {
            Overlay overlay = go.getComponent(Overlay.class);
            if (overlay != null) {
                overlay.resize();
            }
        }
    }

    public boolean isLoaded() {
        return this.sceneIsLoaded;
    }

    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).enableComplexMapKeySerialization()
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("test.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);
            }
            this.sceneIsLoaded = true;
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).enableComplexMapKeySerialization()
                .create();

        // Removes all game objects which should not be serialized
        List<GameObject> gameObjectsToRemove = new ArrayList<>();
        for (GameObject go : gameObjects) {
            if (!go.serializeOnSave()) {
                gameObjectsToRemove.add(go);
            }
        }
        for (GameObject go : gameObjectsToRemove) {
            removeGameObject(go);
        }
        try {
            FileWriter writer = new FileWriter("test.json");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
