package jade;

import components.Sprite;
import components.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float width, float height) {
        GameObject block = new GameObject("Sprite_Object_gen",
                new Transform(new Vector2f(), new Vector2f(width, height)), 0);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);
        return block;
    }
}
