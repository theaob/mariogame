package jade;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {
    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250,0));

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100,100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage.png")));
        addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400,100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage2.png")));
        addGameObjectToScene(obj2);

        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        boolean upPressed = KeyListener.isKeyPressed(GLFW_KEY_UP);
        boolean downPressed = KeyListener.isKeyPressed(GLFW_KEY_DOWN);
        boolean leftPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT);
        boolean rightPressed = KeyListener.isKeyPressed(GLFW_KEY_RIGHT);

        if (upPressed) {
            camera.position.y -= dt * 100.0f;
        }

        if (downPressed) {
            camera.position.y += dt * 100.0f;
        }

        if (leftPressed) {
            camera.position.x += dt * 100.0f;
        }

        if (rightPressed) {
            camera.position.x -= dt * 100.0f;
        }

        System.out.println("FPS " + 1.0f/dt);

        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
