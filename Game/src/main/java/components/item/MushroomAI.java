package components.item;

import components.Component;
import components.player.PlayerController;
import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.RigidBody2D;
import util.AssetPool;

public class MushroomAI extends Component {
    private transient boolean goingRight = true;
    private transient RigidBody2D rb;
    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
    private transient float maxSpeed = 0.8f;
    private transient boolean hitPlayer = false;

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        AssetPool.getSound("powerup_appears.ogg").play();
    }

    @Override
    public void update(float dt) {
        if (goingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
            rb.addVelocity(speed);
        } else if (!goingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
            rb.addVelocity(new Vector2f(-speed.x, speed.y));
        }
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null) {
            contact.setEnabled(false);
            if(!hitPlayer) {
                playerController.powerUp();
                this.gameObject.destroy();
                hitPlayer = true;
            }
        } else if(collidingObject.getComponent(Ground.class) == null) {
                contact.setEnabled(false);
                return;
        }

        if (Math.abs(hitNormal.y) < 0.1f) {
            goingRight = hitNormal.x < 0;
        }
    }
}
