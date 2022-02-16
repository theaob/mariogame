package editor;

import components.NonPickable;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private List<GameObject> activeGameObjectList = new ArrayList<>();
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void imgui() {
        if (activeGameObjectList.size() == 1 && activeGameObjectList.get(0) != null) {
            activeGameObject = activeGameObjectList.get(0);
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (activeGameObject.getComponent(RigidBody2D.class) == null) {
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(CircleCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObjectList.size() == 1 ? this.activeGameObjectList.get(0) : null;
    }

    public void setActiveGameObject(GameObject o) {
        clearSelected();
        if (o != null) {
            activeGameObjectList.add(o);
        }
    }

    public List<GameObject> getActiveGameObjects() {
        return activeGameObjectList;
    }

    public void clearSelected() {
        activeGameObjectList.clear();
    }

    public void addActiveGameObject(GameObject go) {
        if (go != null) {
            this.activeGameObjectList.add(go);
        }
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
