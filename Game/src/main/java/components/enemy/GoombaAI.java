package components.enemy;

import components.Component;
import components.player.PlayerController;
import components.StateMachine;
import components.item.Fireball;
import jade.Camera;
import jade.GameObject;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;
import util.AssetPool;

public class GoombaAI extends Component {

    private transient boolean onGround = false;
    private transient boolean goingRight = false;
    private transient RigidBody2D rb;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(1.6f, 1.6f);
    private transient boolean isDead = false;
    private transient float timeToKill = 0.5f;
    private transient StateMachine stateMachine;

    @Override
    public void start() {
        stateMachine = gameObject.getComponent(StateMachine.class);
        rb = gameObject.getComponent(RigidBody2D.class);
        acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt) {
        Camera camera = Window.getScene().getCamera();
        if (this.gameObject.transform.position.x > camera.position.x +
                (camera.getProjectionSize().x * camera.getZoom())) {
            return;
        }

        if (isDead) {
            timeToKill -= dt;
            if (timeToKill <= 0) {
                this.gameObject.destroy();
            }
            this.rb.setVelocity(new Vector2f());
            return;
        }

        if (goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }

        checkOnGround();

        if (onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.velocity.x = 0;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, terminalVelocity.y), -terminalVelocity.y);
        rb.setVelocity(velocity);
    }

    public void checkOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.14f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        if (isDead) {
            return;
        }

        PlayerController playerController = collidingObject.getComponent(PlayerController.class);

        if (playerController != null) {
            if (!playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    hitNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
            } else if (!playerController.isDead() && !playerController.isInvincible()) {
                playerController.die();
                if(!playerController.isDead()) {
                    contact.setEnabled(false);
                }
            } else if(!playerController.isDead() && playerController.isInvincible()) {
                contact.setEnabled(false);
            }
        } else if (Math.abs(hitNormal.y) < 0.1f) {
            goingRight = hitNormal.x < 0;
        }

        Fireball fb = collidingObject.getComponent(Fireball.class);
        if (fb != null) {
            stomp();
            fb.disappear();
        }
    }

    private void stomp() {
        stomp(true);
    }

    public void stomp(boolean playSound) {
        this.isDead = true;
        this.velocity.zero();
        this.rb.setVelocity(new Vector2f());
        this.rb.setAngularVelocity(0);
        this.rb.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        rb.setSensor();
        if (playSound) {
            AssetPool.getSound("bump.ogg").play();
        }
    }
}
