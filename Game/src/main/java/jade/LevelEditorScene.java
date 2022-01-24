package jade;

import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
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

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100,100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(sprite.getSprite(0)));
        addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400,100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(sprite.getSprite(15)));
        addGameObjectToScene(obj2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"), 16, 16, 26, 0));
    }

    int spriteIndex = 1;
    float spriteFlipTime = 0.05f;
    float spriteFlipTimeLeft = 0.0f;

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

        if(spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if(spriteIndex > 3) {
                spriteIndex = 1;
            }
        }

        spriteFlipTimeLeft -= dt;

        obj1.getComponent(SpriteRenderer.class).setSprite(sprite.getSprite(spriteIndex));

        for (GameObject go : gameObjectList) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
