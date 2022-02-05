package jade;

import editor.PropertiesWindow;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.AssetPool;

import java.awt.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private String title;
    private static Window instance;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private static Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    public float r, g, b, a;

    private static Scene currentScene = null;

    private Window() {
        this.width = 1920;
        this.height = 1000;
        this.title = "Mario";
        r = 1.0F;
        g = 1.0F;
        b = 1.0F;
        a = 1.0F;
    }

    public static Window getInstance() {
        if (instance == null) {
            instance = new Window();
        }
        return instance;
    }

    public static Scene getScene() {
        return currentScene;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene " + newScene;
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static int getWidth() {
        return getInstance().width;
    }

    public static int getHeight() {
        return getInstance().height;
    }

    public static void setWidth(int width) {
        getInstance().width = width;
    }

    public static void setHeight(int height) {
        getInstance().height = height;
    }

    public static Framebuffer getFramebuffer() {
        return framebuffer;
    }

    public void run() {
        System.out.println("LWJGL Version: " + Version.getVersion() + "!");

        init();
        loop();

        //Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate GLFW
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        //Set up the error callback to err stream
        GLFWErrorCallback.createPrint(System.err).set();

        //Init GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        //Set mouse callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        //Set keyboard callbacks
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //TODO: Set gamepad callbacks

        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        //Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        //Enable v-sync
        glfwSwapInterval(1);

        //Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer = new Framebuffer(1920, 1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imguiLayer.initImGui();

        Window.changeScene(0);
    }

    private void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            //poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);

            pickingTexture.enableWriting();
            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();

            glEnable(GL_BLEND);

            // Render pass 2. Render actual game

            DebugDraw.beginFrame();

            framebuffer.bind();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                currentScene.update(dt);
                currentScene.render();
            }
            framebuffer.unbind();

            this.imguiLayer.update(dt, currentScene);

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

        currentScene.saveExit();
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public PropertiesWindow getPropertiesWindow() {
        return imguiLayer.getPropertiesWindow();
    }
}
