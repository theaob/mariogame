package editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import jade.GameObject;
import jade.Window;

import java.util.List;

public class SceneHierarchyWindow {
    public void imgui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjectList = Window.getScene().getGameObjects();
        int index = 0;

        for(GameObject obj : gameObjectList) {
            if(!obj.doSerialization()) {
                continue;
            }

            ImGui.pushID(index);
            boolean treeNodeOpen = ImGui.treeNodeEx(obj.getName(), ImGuiTreeNodeFlags.DefaultOpen |
                    ImGuiTreeNodeFlags.FramePadding |
                    ImGuiTreeNodeFlags.OpenOnArrow |
                    ImGuiTreeNodeFlags.SpanAvailWidth);
            ImGui.popID();

            if(treeNodeOpen) {
                ImGui.treePop();
            }

            index++;
        }

        ImGui.end();
    }
}
