package jade;

import components.Sprite;
import components.SpriteRenderer;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float width, float height) {
        GameObject block = Window.getScene().createGameObject("Sprite_Object_gen");
        block.transform.scale.x = width;
        block.transform.scale.y = height;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);
        return block;
    }
}
