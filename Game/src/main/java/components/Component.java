package components;

import editor.JImGui;
import imgui.ImGui;
import jade.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    private static int ID_COUNTER = 0;
    private int uid = -1;
    public transient GameObject gameObject = null;

    public void update(float dt) {

    }

    public void start() {

    }

    public void imgui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field f : fields) {
                boolean isPrivate = Modifier.isPrivate(f.getModifiers());
                boolean isTransient = Modifier.isTransient(f.getModifiers());

                if (isTransient) {
                    continue;
                }

                if (isPrivate) {
                    f.setAccessible(true);
                }

                Class type = f.getType();
                Object value = f.get(this);
                String name = f.getName();

                if (type == int.class) {
                    int val = (int) value;
                    f.set(this, JImGui.dragInt(name, val));
                } else if (type == float.class) {
                    float val = (float) value;
                    f.set(this, JImGui.dragFloat(name, val));
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    f.set(this, JImGui.checkbox(name, val));
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    JImGui.drawVec2Control(name, val);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                        f.set(this, val);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                        f.set(this, val);
                    }
                }

                if (isPrivate) {
                    f.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void generateId() {
        if (this.uid == -1) {
            this.uid = ID_COUNTER++;
        }

    }

    public int getUid() {
        return this.uid;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void destroy() {

    }

    public void editorUpdate(float dt) {

    }
}
