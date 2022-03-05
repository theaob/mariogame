package components.item;

import components.Component;
import components.player.PlayerController;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.RigidBody2D;
import util.AssetPool;

public class Flower extends Component {
    private transient RigidBody2D rb;

    @Override
    public void start() {
        rb = gameObject.getComponent(RigidBody2D.class);
        AssetPool.getSound("powerup_appears.ogg").play();
        rb.setSensor();
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if(playerController != null) {
            playerController.powerUp();
            this.gameObject.destroy();
        }
    }
}
