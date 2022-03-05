package components.item;

import components.Component;
import components.player.PlayerController;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;

public class Coin extends Component {
    private Vector2f topY;
    private float coinSpeed = 1.4f;
    private transient boolean playAnimation = false;
    private transient boolean playSoundOnce = true;

    @Override
    public void start() {
        topY = new Vector2f(gameObject.transform.position.y).add(0, 0.5f);
    }

    @Override
    public void update(float dt) {
        if(playAnimation) {
            if(playSoundOnce) {
                AssetPool.getSound("coin.ogg").stopAndPlay();
                playSoundOnce = false;
            }

            if(gameObject.transform.position.y < topY.y) {
                gameObject.transform.position.y += dt * coinSpeed;
                gameObject.transform.scale.x -= (0.5f * dt) % -1.0f;
            } else {
                gameObject.destroy();

            }
        }
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if(collidingObject.getComponent(PlayerController.class) != null) {
            playAnimation = true;
            contact.setEnabled(false);
        }
    }
}
