package components;

import jade.GameObject;
import jade.KeyListener;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import renderer.DebugDraw;
import renderer.PickingTexture;
import util.Settings;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null;
    private float debounceTime = 0.09f;
    private float debounce = 0;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    public void pickupObject(GameObject object) {
        if (this.holdingObject != null) {
            this.holdingObject.destroy();
        }
        this.holdingObject = object;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.holdingObject.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(this.holdingObject);
    }

    public void place() {
        GameObject newObj = this.holdingObject.copy();
        if (newObj.getComponent(StateMachine.class) != null) {
            newObj.getComponent(StateMachine.class).refreshTextures();
        }
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        newObj.removeComponent(NonPickable.class);
        Window.getScene().addGameObjectToScene(newObj);
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PickingTexture pickingTexture = Window.getInstance().getPropertiesWindow().getPickingTexture();

        if (holdingObject != null && debounce <= 0) {
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();

            holdingObject.transform.position.x = ((int) Math.floor(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int) Math.floor(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
                debounce = debounceTime;
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        } else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = Window.getScene().getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                Window.getInstance().getPropertiesWindow().setActiveGameObject(pickedObj);
            } else if (pickedObj == null & !MouseListener.isDragging()) {
                Window.getInstance().getPropertiesWindow().clearSelected();
            }
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!boxSelectSet) {
                Window.getInstance().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }

            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize = (new Vector2f(boxSelectEndWorld)).sub(boxSelectStartWorld).mul(0.5f);
            DebugDraw.addBox2D(new Vector2f(boxSelectStartWorld).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f), 0);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int) boxSelectStart.x;
            int screenStartY = (int) boxSelectStart.y;
            int screenEndX = (int) boxSelectEnd.x;
            int screenEndY = (int) boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int temp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = temp;
            }

            if (screenEndY < screenStartY) {
                int temp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = temp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );

            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for(float objectID : gameObjectIds) {
                uniqueGameObjectIds.add((int)objectID);
            }

            for(Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObject = Window.getScene().getGameObject(gameObjectId);
                if(pickedObject != null && gameObject.getComponent(NonPickable.class) == null) {
                    Window.getInstance().getPropertiesWindow().addActiveGameObject(pickedObject);
                }
            }
        }
    }
}
