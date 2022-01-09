package jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[360];

    private KeyListener()
    {

    }

    public static KeyListener getInstance() {
        if(instance == null)
        {
            instance = new KeyListener();
        }
        return instance;
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int modifiers)
    {
        if(key < getInstance().keyPressed.length)
        {
            if(action == GLFW_PRESS)
            {
                getInstance().keyPressed[key] = true;
            }
            else if(action == GLFW_RELEASE)
            {
                getInstance().keyPressed[key] = false;
            }
        }
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return getInstance().keyPressed[keyCode];
    }
}
