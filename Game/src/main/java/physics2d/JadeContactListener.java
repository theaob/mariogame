package physics2d;

import components.Component;
import jade.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class JadeContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureA().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for(Component c : objectA.getAllComponents()) {
            c.beginCollision(objectB, contact, aNormal);
        }

        for(Component c : objectB.getAllComponents()) {
            c.beginCollision(objectA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureA().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for(Component c : objectA.getAllComponents()) {
            c.endCollision(objectB, contact, aNormal);
        }

        for(Component c : objectB.getAllComponents()) {
            c.endCollision(objectA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureA().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for(Component c : objectA.getAllComponents()) {
            c.preSolve(objectB, contact, aNormal);
        }

        for(Component c : objectB.getAllComponents()) {
            c.preSolve(objectA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureA().getUserData();

        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for(Component c : objectA.getAllComponents()) {
            c.postSolve(objectB, contact, aNormal);
        }

        for(Component c : objectB.getAllComponents()) {
            c.postSolve(objectA, contact, bNormal);
        }
    }
}
