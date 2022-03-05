package components.item;

import components.player.PlayerController;
import util.AssetPool;

public class BreakableBrick extends Block {

    @Override
    public void playerHit(PlayerController playerController) {
        if (playerController.isSmall()) {
            AssetPool.getSound("bump.ogg").play();
        } else {
            AssetPool.getSound("break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
