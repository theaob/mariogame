package components;

import jade.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public abstract class Block extends Component {
    private transient boolean blockGoingUp = true;
    private transient boolean doAnimation = false;
    private transient Vector2f bopStart;
    private transient Vector2f topBopLocation;
    private transient boolean active = true;

    public float bopSpeed = 0.4f;

    @Override
    public void start() {
        this.bopStart = new Vector2f(this.gameObject.transform.position);
        this.topBopLocation = new Vector2f(bopStart).add(0.0f, 0.02f);
    }

    @Override
    public void update(float dt) {
        if (doAnimation) {
            if (blockGoingUp) {
                if (this.gameObject.transform.position.y < topBopLocation.y) {
                    this.gameObject.transform.position.y += bopSpeed * dt;
                } else {
                    blockGoingUp = false;
                }
            } else {
                if (this.gameObject.transform.position.y > bopStart.y) {
                    this.gameObject.transform.position.y -= bopSpeed * dt;
                } else {
                    this.gameObject.transform.position.y = this.bopStart.y;
                    blockGoingUp = true;
                    doAnimation = false;
                }
            }
        }
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (active && (playerController != null) && hitNormal.y < -0.8f) {
            doAnimation = true;
            playerHit(playerController);
        }
    }

    public void setInactive() {
        this.active = false;
    }

    abstract void playerHit(PlayerController playerController);
}
