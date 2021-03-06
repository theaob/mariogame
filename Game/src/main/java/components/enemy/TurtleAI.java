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

public class TurtleAI extends Component {
    private transient boolean goingRight = false;
    private transient RigidBody2D rb;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient boolean isDead = false;
    private transient boolean isMoving = false;
    private transient StateMachine stateMachine;
    private float movingDebounce = 0.32f;

    @Override
    public void start() {
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y = 0.7f;
    }

    @Override
    public void update(float dt) {
        movingDebounce -= dt;
        Camera camera = Window.getScene().getCamera();
        if (this.gameObject.transform.position.x > camera.position.x
                + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        if(!isDead || isMoving) {
            if(goingRight) {
                velocity.x = walkSpeed;
                gameObject.transform.scale.x = -0.25f;
                acceleration.x = 0;
            } else {
                velocity.x = -walkSpeed;
                gameObject.transform.scale.x = 0.25f;
                acceleration.x = 0;
            }
        } else {
            velocity.x = 0;
        }

        checkOnGround();

        //TODO: Move this logic to Physics Component for reuse
        if(onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rb.setVelocity(this.velocity);

        //TODO: Destroy if Turtle falls of the world
    }

    public void checkOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.2f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    public void stomp() {
        this.isDead = true;
        this.isMoving = false;
        this.velocity.zero();
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0.0f);
        this.rb.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        AssetPool.getSound("bump.ogg").play();
    }

    @Override
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        GoombaAI goomba = collidingObject.getComponent(GoombaAI.class);
        if (isDead && isMoving && goomba != null) {
            goomba.stomp(false);
            contact.setEnabled(false);
            AssetPool.getSound("kick.ogg").play();
        }

        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null) {
            if (!isDead && !playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    hitNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
                walkSpeed *= 3.0f;
            } else if (movingDebounce < 0 && !playerController.isDead() &&
                    !playerController.isHurtInvincible() &&
                    (isMoving || !isDead) && hitNormal.y < 0.58f) {
                playerController.die();
                if (!playerController.isDead()) {
                    contact.setEnabled(false);
                }
            } else if (!playerController.isDead() && !playerController.isHurtInvincible()) {
                if (isDead && hitNormal.y > 0.58f) {
                    playerController.enemyBounce();
                    isMoving = !isMoving;
                    goingRight = hitNormal.x < 0;
                } else if (isDead && !isMoving) {
                    isMoving = true;
                    goingRight = hitNormal.x < 0;
                    movingDebounce = 0.32f;
                }
            } else if (!playerController.isDead() && playerController.isHurtInvincible()) {
                contact.setEnabled(false);
            }
        } else if (Math.abs(hitNormal.y) < 0.1f && !collidingObject.isDead()) {
            goingRight = hitNormal.x < 0;
            if (isMoving && isDead) {
                AssetPool.getSound("bump.ogg").play();
            }
        }

        Fireball fb = collidingObject.getComponent(Fireball.class);
        if (fb != null) {
            if (!isDead) {
                walkSpeed *= 3.0f;
                stomp();
            } else {
                isMoving = !isMoving;
                goingRight = hitNormal.x < 0;
            }
            collidingObject.getComponent(Fireball.class).disappear();
            contact.setEnabled(false);
        }
    }
}
