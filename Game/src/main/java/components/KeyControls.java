package components;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import util.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {

    private float debounceTime = 0.2f;
    private float debounce = 0.0f;

    @Override
    public void editorUpdate(float dt) {
        debounce -=dt;

        PropertiesWindow propertiesWindow = Window.getInstance().getPropertiesWindow();
        GameObject activeGameObject = Window.getInstance().getPropertiesWindow().getActiveGameObject();
        List<GameObject> activeGameObjectList = propertiesWindow.getActiveGameObjects();

        float multiplier = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f  : 1.0f;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) &&
                activeGameObject != null) {
            GameObject newGo = activeGameObject.copy();
            newGo.transform.position.add(Settings.GRID_WIDTH, 0);
            Window.getScene().addGameObjectToScene(newGo);
            propertiesWindow.setActiveGameObject(newGo);
            if(newGo.getComponent(StateMachine.class) != null) {
                newGo.getComponent(StateMachine.class).refreshTextures();
            }
        } else if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) &&
                activeGameObjectList.size() > 0)) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjectList);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
                if(go.getComponent(StateMachine.class) != null) {
                    go.getComponent(StateMachine.class).refreshTextures();
                }
            }
        } else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjectList) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if(KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
            debounce= debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.zIndex--;
            }
        } else if(KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce= debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.zIndex++;
            }
        } else if(KeyListener.isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
            debounce= debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        } else if(KeyListener.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
            debounce= debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.x -= Settings.GRID_HEIGHT * multiplier;
            }
        } else if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce= debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.x += Settings.GRID_HEIGHT * multiplier;
            }
        } else if(KeyListener.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
            debounce= debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        }
    }
}
