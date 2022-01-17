package jade;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene() {
        System.out.println("Inside LevelEditorScene");
    }

    @Override
    public void update(float dt) {
        if (!changingScene) {
            changingScene = true;
        }

        if(changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.getInstance().r -= dt * 0.5f;
            Window.getInstance().g -= dt * 0.5f;
            Window.getInstance().b -= dt * 0.5f;
        }
        else if(changingScene) {
            Window.changeScene(1);
        }
    }
}
