package components;

import util.AssetPool;

public class BreakableBrick extends Block {

    @Override
    void playerHit(PlayerController playerController) {
        if (playerController.isSmall()) {
            AssetPool.getSound("bump.ogg").play();
        } else {
            AssetPool.getSound("break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
