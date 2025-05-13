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
import secondEngine.components.Overlay;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.StateMachine;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;
import secondEngine.renderer.Renderer;
import secondEngine.util.ComponentDeserializer;
import secondEngine.util.GameObjectDeserializer;

public abstract class Scene {
    protected Renderer renderer = new Renderer();

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected Camera camera = null;
    protected float[] vertexArray;
    protected int[] elementArray;

    private boolean isRunning = false;

    private boolean sceneIsLoaded = false;

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

    public GameObject createGameObject(String name) {
        return createGameObject(name, new Transform());
    }

    public GameObject createGameObject(String name, Transform transform) {
        GameObject go = new GameObject(name);

        go.addComponent(transform);
        go.transform = go.getComponent(Transform.class);
        return go;
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
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("test.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i=0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);
            }
            this.sceneIsLoaded = true;
        }
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        try {
            FileWriter writer = new FileWriter("test.json");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
