package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import imgui.ImGui;
import jade.Camera;
import jade.GameObject;
import jade.GameObjectDeserializer;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjectList;
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false;

    public Scene() {

        gameObjectList = new ArrayList<>();
    }

    public void init() {

    }

    public void start() {
        for (GameObject go : gameObjectList) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjectList.add(go);

        if (isRunning) {
            go.start();
            this.renderer.add(go);

        }
    }

    public abstract void update(float dt);

    public abstract void render();

    public Camera getCamera() {
        return camera;
    }

    public void sceneImgui() {
        if(activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui() {

    }

    public void saveExit() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjectList));
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

        if(!inFile.equals("")) {
            int maxGoId = -1, maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for(Component c : objs[i].getAllComponents()) {
                    if(c.getUid() > maxCompId) {
                        maxCompId = c.getUid();
                    }
                }

                if(objs[i].getUid() > maxGoId) {
                    maxGoId = objs[i].getUid();
                }
            }

            maxCompId++;
            maxGoId++;

            GameObject.init(maxGoId);
            Component.init(maxCompId);
            this.levelLoaded = true;
        }
    }
}
