package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {
    GameObject obj1;
    Spritesheet sprites;

    MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            this.activeGameObject = gameObjectList.get(0);
            return;
        }


        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 8);
        SpriteRenderer obj1Renderer = new SpriteRenderer();
        obj1Renderer.setColor(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Renderer);
        obj1.addComponent(new Rigidbody());
        addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 4);
        SpriteRenderer obj2Renderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        obj2Renderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2Renderer);
        addGameObjectToScene(obj2);

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");
    }

    float t = 0.0f;

    @Override
    public void update(float dt) {
        boolean upPressed = KeyListener.isKeyPressed(GLFW_KEY_UP);
        boolean downPressed = KeyListener.isKeyPressed(GLFW_KEY_DOWN);
        boolean leftPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT);
        boolean rightPressed = KeyListener.isKeyPressed(GLFW_KEY_RIGHT);

        float x = ((float) Math.sin(t) * 200.0f) + 600;
        float y = ((float) Math.cos(t) * 200.0f) + 400;
        t += 0.05f;

        DebugDraw.addLine2D(new Vector2f(600,400), new Vector2f(x,y), new Vector3f(0,0,1), 10);

        mouseControls.update(dt);

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

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTextId();
            Vector2f[] texCoords = sprite.getTextureCoordinates();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                // Attach this to cursor
                mouseControls.pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}
