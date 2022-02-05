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

    GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

        this.camera = new Camera(new Vector2f(-250, 0));

        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(this.camera));
        levelEditorStuff.addComponent(new TranslateGizmo(gizmos.getSprite(1),
                Window.getInstance().getPropertiesWindow()));
        levelEditorStuff.start();
/*

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
        addGameObjectToScene(obj2);*/

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");
        AssetPool.addSpriteSheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));

        for (GameObject go : gameObjectList) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }

    float angle = 0.0f, x = 0.0f, y = 0.0f;

    @Override
    public void update(float dt) {
        boolean upPressed = KeyListener.isKeyPressed(GLFW_KEY_UP);
        boolean downPressed = KeyListener.isKeyPressed(GLFW_KEY_DOWN);
        boolean leftPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT);
        boolean rightPressed = KeyListener.isKeyPressed(GLFW_KEY_RIGHT);

        levelEditorStuff.update(dt);

        this.camera.adjustProjection();

        DebugDraw.addBox2D(new Vector2f(200, 200), new Vector2f(64, 32), angle, new Vector3f(0, 1, 0), 1);
        angle += 40.0f * dt;

        DebugDraw.addCircle(new Vector2f(x, y), 64, new Vector3f(0, 1, 0), 1);
        x += 50f * dt;
        y += 50f * dt;

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
    }

    @Override
    public void render() {
        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

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
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTextId();
            Vector2f[] texCoords = sprite.getTextureCoordinates();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                // Attach this to cursor
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
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
