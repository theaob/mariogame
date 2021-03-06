package jade;

import jade.Sprite;
import org.joml.Vector2f;
import renderer.Texture;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {
    private Texture texture;
    private List<Sprite> spriteList;

    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        spriteList = new ArrayList<>();
        this.texture = texture;
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;

        for(int i = 0; i < numSprites; i++) {
            float topY = (currentY + spriteHeight) / (float)texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float)texture.getWidth();
            float leftX = currentX / (float)texture.getWidth();
            float bottomY = currentY / (float)texture.getHeight();

            Vector2f[] texCoords = {
                    new Vector2f(rightX,topY),
                    new Vector2f(rightX,bottomY),
                    new Vector2f(leftX,bottomY),
                    new Vector2f(leftX,topY)
            };

            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTextureCoordinates(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            spriteList.add(sprite);

            currentX += spriteWidth + spacing;

            if(currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return this.spriteList.get(index);
    }

    public int size(){
        return spriteList.size();
    }
}
