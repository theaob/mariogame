package jade;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjectList;

    public Scene() {

        gameObjectList = new ArrayList<>();
    }

    public void init() {

    }

    public void start() {
        for (GameObject go : gameObjectList) {
            go.start();
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjectList.add(go);

        if (isRunning) {
            go.start();
        }
    }

    public abstract void update(float dt);


}
