package components;

import components.player.PlayerController;
import jade.Camera;
import jade.GameObject;
import jade.Window;
import org.joml.Vector4f;

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera sceneCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel = 0.0f;
    private transient float cameraHeight = 1.5f;
    private transient float playerHeight = 0.25f;

    private final Vector4f SKY_COLOR = new Vector4f(92.0f / 255.0f, 148.0f / 255.0f, 252.0f / 255.0f, 1.0f);
    private final Vector4f UNDERGROUND_COLOR = new Vector4f(0f, 0f, 0f, 1.0f);

    public GameCamera(Camera sceneCamera) {
        this.sceneCamera = sceneCamera;
    }

    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.sceneCamera.clearColor.set(SKY_COLOR);
        this.undergroundYLevel = this.sceneCamera.position.y
                - this.sceneCamera.getProjectionSize().y
                - this.cameraHeight;
    }

    @Override
    public void update(float dt) {
        if (player != null && !player.getComponent(PlayerController.class).hasWon()) {
            sceneCamera.position.x = player.transform.position.x - 2.5f;
            //sceneCamera.position.x = Math.max(player.transform.position.x - 2.5f, highestX);
            //highestX = Math.max(highestX, sceneCamera.position.x);

            if(player.transform.position.y < -playerHeight) {
                this.sceneCamera.position.y = undergroundYLevel;
                this.sceneCamera.clearColor.set(UNDERGROUND_COLOR);
            } else if(player.transform.position.y >= 0.0f) {
                this.sceneCamera.position.y = 0;
                this.sceneCamera.clearColor.set(SKY_COLOR);
            }
        }
    }
}
