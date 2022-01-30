package components;

import org.joml.Vector2f;
import renderer.Texture;

public class Sprite {
    private Texture texture = null;
    private Vector2f[] textureCoordinates = {
            new Vector2f(1,1),
            new Vector2f(1,0),
            new Vector2f(0,0),
            new Vector2f(0,1)
    };;

/*    public Sprite(Texture texture) {
        this.texture = texture;
        Vector2f[] texCoords = {
                new Vector2f(1,1),
                new Vector2f(1,0),
                new Vector2f(0,0),
                new Vector2f(0,1)
        };
        this.textureCoordinates = texCoords;
    }

    public Sprite(Texture texture, Vector2f[] textureCoordinates) {
        this.texture = texture;
        this.textureCoordinates = textureCoordinates;
    }*/

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTextureCoordinates() {
        return this.textureCoordinates;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTextureCoordinates(Vector2f[] texCoords) {
        this.textureCoordinates = texCoords;
    }
}
