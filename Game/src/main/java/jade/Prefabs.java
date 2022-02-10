package jade;

import components.*;
import util.AssetPool;

import javax.swing.plaf.nimbus.State;

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

    public static GameObject generateMario() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionBlock() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/items.png");
        GameObject questionBlock = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState();
        run.title = "Flicker";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), 0.57f);
        run.addFrame(playerSprites.getSprite(1), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }
}
