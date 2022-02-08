package editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import jade.GameObject;
import jade.Window;

import java.util.List;

public class SceneHierarchyWindow {
    private final String DRAG_DROP_STRING = "SceneHierarchy";

    public void imgui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjectList = Window.getScene().getGameObjects();
        int index = 0;

        for(GameObject obj : gameObjectList) {
            if(!obj.doSerialization()) {
                continue;
            }

            if(doTreeNode(obj, index)) {
                ImGui.treePop();
            }

            index++;
        }

        ImGui.end();
    }

    private boolean doTreeNode(GameObject go, int index) {
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(go.name, ImGuiTreeNodeFlags.DefaultOpen |
                ImGuiTreeNodeFlags.FramePadding |
                ImGuiTreeNodeFlags.OpenOnArrow |
                ImGuiTreeNodeFlags.SpanAvailWidth);
        ImGui.popID();

        if(ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(DRAG_DROP_STRING, go);
            ImGui.text(go.name);
            ImGui.endDragDropSource();
        }

        if(ImGui.beginDragDropTarget()) {
            GameObject payload = ImGui.acceptDragDropPayload(DRAG_DROP_STRING, GameObject.class);
            if(payload != null) {
                //getClass().isAssignableFrom(GameObject.class) ??
                System.out.println("Payload " + payload.name);
            }
            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }
}
