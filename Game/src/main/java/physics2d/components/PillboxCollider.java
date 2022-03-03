package physics2d.components;

import components.Component;
import jade.Window;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;

public class PillboxCollider extends Component {
    private transient Box2DCollider box = new Box2DCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();
    private transient boolean resetFixtureNextFrame = false;

    public float width = 0.1f;
    public float height = 0.2f;
    private Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return offset;
    }

    @Override
    public void start() {
        this.box.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        recalculateColliders();
    }

    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);

        recalculateColliders();

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }

        resetFixtureNextFrame = false;

        if (gameObject != null) {
            RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
            if (rb != null) {
                Window.getPhysics().resetPillboxCollider(rb, this);
            }
        }
    }

    private void recalculateColliders() {
        float circleRadius = width / 2.0f;
        float boxHeight = height - circleRadius;
        bottomCircle.setRadius(circleRadius);
        bottomCircle.setOffset(new Vector2f(offset).sub(0, (height - circleRadius * 2.0f) / 2.0f));
        box.setHalfSize(new Vector2f(width - 0.01f, boxHeight));
        box.setOffset(new Vector2f(offset).add(0, (height - boxHeight) / 2.0f));
    }

    public Box2DCollider getBox() {
        return box;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public void setWidth(float width) {
        this.width = width;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float height) {
        this.height = height;
        recalculateColliders();
        resetFixture();
    }
}
