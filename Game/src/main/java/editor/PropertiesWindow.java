package editor;

import components.SpriteRenderer;
import imgui.ImGui;
import jade.GameObject;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
    private final List<GameObject> activeGameObjectList = new ArrayList<>();
    private final List<Vector4f> activeGameObjectOriginalColors = new ArrayList<>();
    private final PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void imgui() {
        if (activeGameObjectList.size() == 1 && activeGameObjectList.get(0) != null) {
            GameObject activeGameObject = activeGameObjectList.get(0);
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
        if(activeGameObjectOriginalColors.size() > 0) {
            int i = 0;
            for(GameObject go : activeGameObjectList) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if(spr != null) {
                    spr.setColor(activeGameObjectOriginalColors.get(i));
                    i++;
                }
            }
        }

        activeGameObjectList.clear();
        activeGameObjectOriginalColors.clear();
    }

    public void addActiveGameObject(GameObject go) {
        if (go != null) {
            this.activeGameObjectList.add(go);

            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if(spr != null) {
                activeGameObjectOriginalColors.add(new Vector4f(spr.getColor()));
                spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
            } else {
                activeGameObjectOriginalColors.add(new Vector4f());
            }
        }
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
