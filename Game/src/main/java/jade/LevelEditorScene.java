package jade;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {
    GameObject obj1;
    Spritesheet sprite;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250,0));
        loadResources();

        sprite = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200,100), new Vector2f(256, 256)),8);
        //obj1.addComponent(new SpriteRenderer(sprite.getSprite(0)));
        obj1.addComponent(new SpriteRenderer(new Vector4f(1,0,0,1)));
        addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400,100), new Vector2f(256, 256)),4);
        //obj2.addComponent(new SpriteRenderer(sprite.getSprite(15)));
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage2.png"))));
        addGameObjectToScene(obj2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"), 16, 16, 26, 0));
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

        //System.out.println("FPS " + 1.0f/dt);

        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test window");
        ImGui.text("Random text");
        ImGui.end();
    }
}
