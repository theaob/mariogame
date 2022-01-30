package scenes;

import jade.Window;
import scenes.Scene;

public class LevelScene extends Scene {
    private float timeToChangeScene = 2.0f;

    public LevelScene() {
        System.out.println("Inside Level Scene");
    }

    @Override
    public void update(float dt) {
        if(timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.getInstance().r += dt * 0.5f;
            Window.getInstance().g += dt * 0.5f;
            Window.getInstance().b += dt * 0.5f;
        }
    }
}
