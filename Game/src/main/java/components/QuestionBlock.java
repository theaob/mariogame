package components;

import jade.GameObject;
import jade.Prefabs;
import jade.Sound;
import jade.Window;
import util.AssetPool;

import javax.swing.plaf.nimbus.State;

public class QuestionBlock extends Block {

    private BlockType blockType = BlockType.Coin;
    private int hitCount = 10;
    private boolean multipleHit = true;

    @Override
    void playerHit(PlayerController playerController) {
        switch (blockType) {
            case Coin -> {
                doCoin(playerController);
            }
            case Powerup -> {
                doPowerup(playerController);
            }
            case Invincibility -> {
                doInvincibility(playerController);
            }
        }

        if(multipleHit && hitCount >0) {
            hitCount--;
        } else {
            StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
            if(stateMachine != null) {
                stateMachine.trigger("setInactive");
                setInactive();
            }
        }

    }

    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(coin);
    }



    private void doPowerup(PlayerController playerController) {
        if (playerController.isSmall()) {
            spawnMushroom();
        } else {
            spawnFlower();
        }
    }

    private void doInvincibility(PlayerController playerController) {

    }


    private void spawnFlower() {
        GameObject flower = Prefabs.generateFlower();
        flower.transform.position.set(gameObject.transform.position);
        flower.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(flower);
    }

    private void spawnMushroom() {
        GameObject mushroom = Prefabs.generateMushroom();
        mushroom.transform.position.set(gameObject.transform.position);
        mushroom.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(mushroom);
    }


}
