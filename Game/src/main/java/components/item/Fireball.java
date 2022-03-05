package components.item;

import components.Component;
import components.player.PlayerController;
import jade.GameObject;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;

public class Fireball extends Component {
    private static int fireballCount = 0;
    public transient boolean goingRight = false;
    private transient RigidBody2D rb;
    private transient float fireballSpeed = 1.7f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient float lifeTime = 4.0f;

    public static boolean canSpawn() {
        return fireballCount < 5;
    }

    @Override
    public void start() {
        fireballCount++;
        rb = gameObject.getComponent(RigidBody2D.class);
        acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt) {
        lifeTime -= dt;

        if (lifeTime <= 0) {
            disappear();
            return;
        }

        if (goingRight) {
            velocity.x = fireballSpeed;
        } else {
            velocity.x = -fireballSpeed;
        }

        checkOnGround();

        if (onGround) {
            this.acceleration.y = 1.5f;
            this.velocity.y = 2.5f;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, terminalVelocity.y), -terminalVelocity.y);
        rb.setVelocity(velocity);
    }

    public void checkOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.09f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (collidingObject.getComponent(PlayerController.class) != null ||
                collidingObject.getComponent(Fireball.class) != null) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (Math.abs(hitNormal.x) > 0.8f) {
            goingRight = hitNormal.x < 0;
        }
    }

    public void disappear() {
        fireballCount--;
        gameObject.destroy();
    }
}
