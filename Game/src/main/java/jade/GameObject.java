package jade;

import components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;
    private String name;
    private List<Component> componentList;
    public transient Transform transform;
    private boolean doSerialization = true;

    public GameObject(String name) {
        this.name = name;
        this.componentList = new ArrayList<>();

        this.uid = ID_COUNTER++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : componentList) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error : Casting component";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < componentList.size(); i++) {
            Component c = componentList.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                componentList.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateId();
        componentList.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for(int i = 0; i < componentList.size(); i++)
        {
            componentList.get(i).update(dt);
        }
    }

    public void start() {
        for(int i = 0; i < componentList.size(); i++)
        {
            componentList.get(i).start();
        }
    }

    public void imgui() {
        for (Component c :
                componentList) {
            c.imgui();
        }
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public int getUid() {
        return this.uid;
    }

    public List<Component> getAllComponents() {
        return this.componentList;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean doSerialization() {
        return this.doSerialization;
    }
}
