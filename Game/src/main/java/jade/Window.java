package jade;

import editor.GameViewWindow;
import editor.PropertiesWindow;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.LevelSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private int width, height;
    private String title;
    private static Window instance;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private static Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    private long audioContext;
    private long audioDevice;

    private static Scene currentScene = null;
    private boolean runtimePlaying = false;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        EventSystem.addObserver(this);
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

    public static void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }


        currentScene = new Scene(sceneInitializer);
        Window.getInstance().imguiLayer.getPropertiesWindow().setActiveGameObject(null);
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

    public static Physics2D getPhysics() {
        return currentScene.getPhysics();
    }

    public void run() {
        System.out.println("LWJGL Version: " + Version.getVersion() + "!");

        init();
        loop();

        //Destroy audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

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

        //Initialize audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            assert false : "Audio Library Not Supported!";
        }

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

        Window.changeScene(new LevelEditorSceneInitializer());
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

            glClearColor(0,0,0,0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();

            glEnable(GL_BLEND);

            // Render pass 2. Render actual game

            DebugDraw.beginFrame();

            framebuffer.bind();

            Vector4f clearColor = currentScene.getCamera().clearColor;
            glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
                DebugDraw.draw();
            }
            framebuffer.unbind();

            this.imguiLayer.update(dt, currentScene);

            glfwSwapBuffers(glfwWindow);

            MouseListener.endFrame();
            KeyListener.endFrame();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public PropertiesWindow getPropertiesWindow() {
        return imguiLayer.getPropertiesWindow();
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelSceneInitializer());
                break;
            case GameEnginStopPlay:
                this.runtimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
        }
    }

    public GameViewWindow getGameViewWindow() {
        return Window.getInstance().imguiLayer.getGameViewWindow();
    }
}
