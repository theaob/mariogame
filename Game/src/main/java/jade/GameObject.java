package jade;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> componentList;
    public Transform transform;

    public GameObject(String name) {
        this.name = name;
        this.componentList = new ArrayList<>();
        this.transform = new Transform();
    }

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.componentList = new ArrayList<>();
        this.transform = transform;
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
}
