package jade;

import imgui.ImGui;
import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjectList;
    protected GameObject activeGameObject = null;

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
}
