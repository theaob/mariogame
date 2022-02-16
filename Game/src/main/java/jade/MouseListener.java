package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, worldX, worldY;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private int mouseButtonDown = 0;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        scrollX = 0.0;
        scrollY = 0.0;
        xPos = 0.0;
        yPos = 0.0;

        isDragging = false;
    }

    public static MouseListener getInstance() {
        if (instance == null) {
            instance = new MouseListener();
        }
        return instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        if (!Window.getInstance().getGameViewWindow().getWantCaptureMouse()) {
            clear();
        }

        if(getInstance().mouseButtonDown > 0) {
            getInstance().isDragging = true;
        }

        getInstance().xPos = xPos;
        getInstance().yPos = yPos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int modifiers) {
        if (action == GLFW_PRESS) {
            getInstance().mouseButtonDown++;

            if (button < getInstance().mouseButtonPressed.length) {
                getInstance().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            getInstance().mouseButtonDown--;

            if (button < getInstance().mouseButtonPressed.length) {
                getInstance().mouseButtonPressed[button] = false;
                getInstance().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        getInstance().scrollX = xOffset;
        getInstance().scrollY = yOffset;
    }

    public static void endFrame() {
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
    }

    public static void clear() {
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
        getInstance().xPos = 0.0;
        getInstance().yPos = 0.0;
        getInstance().mouseButtonDown = 0;
        getInstance().isDragging = false;
        Arrays.fill(getInstance().mouseButtonPressed, false);
    }

    public static float getX() {
        return (float) getInstance().xPos;
    }

    public static float getY() {
        return (float) getInstance().yPos;
    }

    public static float getScrollX() {
        return (float) getInstance().scrollX;
    }

    public static float getScrollY() {
        return (float) getInstance().scrollY;
    }

    public static boolean isDragging() {
        return getInstance().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < getInstance().mouseButtonPressed.length) {
            return getInstance().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    public static Vector2f getWorld() {
        float currentX = getX() - getInstance().gameViewportPos.x;
        float currentY = getY() - getInstance().gameViewportPos.y;

        currentX = (currentX / getInstance().gameViewportSize.x) * 2.0f - 1.0f;
        currentY = -((currentY / getInstance().gameViewportSize.y) * 2.0f - 1.0f);

        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Camera camera = Window.getScene().getCamera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static float getWorldX() {
        return getWorld().x;
    }

    public static float getWorldY() {
        return getWorld().y;
    }

    public static Vector2f screenToWorld(Vector2f screenCoordinates) {
        Vector2f normalizedScreenCoordinates = new Vector2f();
        normalizedScreenCoordinates.set(screenCoordinates.x / Window.getWidth(),
                screenCoordinates.y / Window.getHeight());

        //Normalize tp [-1,1]
        normalizedScreenCoordinates.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));

        Camera camera = Window.getScene().getCamera();
        Vector4f temp = new Vector4f(normalizedScreenCoordinates.x, normalizedScreenCoordinates.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        temp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(temp.x, temp.y);
    }

    public static Vector2f worldToScreen(Vector2f worldCoordinates) {
        Camera camera = Window.getScene().getCamera();
        Vector4f ndcSpacePosition = new Vector4f(worldCoordinates.x, worldCoordinates.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePosition.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePosition.x, ndcSpacePosition.y).mul(1.0f / ndcSpacePosition.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));
        return windowSpace;
    }

    public static float getScreenX() {
        return getScreen().x;
    }

    public static float getScreenY() {
        return getScreen().y;
    }

    public static Vector2f getScreen() {
        float currentX = getX() - getInstance().gameViewportPos.x;
        currentX = (currentX / getInstance().gameViewportSize.x) * 1920;
        float currentY = getY() - getInstance().gameViewportPos.y;
        currentY = 1080 - ((currentY / getInstance().gameViewportSize.y) * 1080);

        return new Vector2f(currentX, currentY);
    }

    public void setGameViewportPos(Vector2f gameViewportPos) {
        this.gameViewportPos.set(gameViewportPos);
    }

    public void setGameViewportSize(Vector2f gameViewportSize) {
        this.gameViewportSize.set(gameViewportSize);
    }
}
