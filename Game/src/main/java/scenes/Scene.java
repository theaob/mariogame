package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import jade.Camera;
import jade.GameObject;
import jade.GameObjectDeserializer;
import jade.Transform;
import org.joml.Vector2f;
import physics2d.Physics2D;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {
    private final Renderer renderer;
    private Camera camera;
    private boolean isRunning = false;
    private final List<GameObject> gameObjectList;
    private final Physics2D physics2D;

    private final SceneInitializer sceneInitializer;

    public Scene(SceneInitializer initializer) {
        this.sceneInitializer = initializer;
        this.physics2D = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjectList = new ArrayList<>();
    }

    public void init() {
        this.camera = new Camera(new Vector2f(0, 0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjectList.size(); i++) {
            GameObject go = gameObjectList.get(i);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjectList.add(go);

        if (isRunning) {
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
    }

    public void editorUpdate(float dt) {
        this.camera.adjustProjection();

        for (int i = 0; i < gameObjectList.size(); i++) {
            GameObject go = gameObjectList.get(i);
            go.editorUpdate(dt);

            if(go.isDead()) {
                gameObjectList.remove(go);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    public void update(float dt) {
        this.camera.adjustProjection();
        this.physics2D.update(dt);

        for (int i = 0; i < gameObjectList.size(); i++) {
            GameObject go = gameObjectList.get(i);
            go.update(dt);

            if(go.isDead()) {
                gameObjectList.remove(go);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    public void render() {
        this.renderer.render();
    }

    public Camera getCamera() {
        return camera;
    }

    public void imgui() {
        this.sceneInitializer.imgui();
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject object : this.gameObjectList) {
                if (object.doSerialization()) {
                    objsToSerialize.add(object);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String inFile = "";

        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));

        } catch (NoSuchFileException e) {
            System.out.println("Levels not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoId = -1, maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for (Component c : objs[i].getAllComponents()) {
                    if (c.getUid() > maxCompId) {
                        maxCompId = c.getUid();
                    }
                }

                if (objs[i].getUid() > maxGoId) {
                    maxGoId = objs[i].getUid();
                }
            }

            maxCompId++;
            maxGoId++;

            GameObject.init(maxGoId);
            Component.init(maxCompId);
        }
    }

    public void destroy() {
        for (GameObject go : gameObjectList) {
            go.destroy();
        }
    }

    public GameObject getGameObject(int gameObjectID) {
        Optional<GameObject> result = gameObjectList.stream().
                filter(gameObject -> gameObject.getUid() == gameObjectID).findFirst();
        return result.orElse(null);
    }

    public List<GameObject> getGameObjects() {
        return gameObjectList;
    }

    public Physics2D getPhysics() {
        return physics2D;
    }
}
