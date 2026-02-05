package blocks;

public class WaterBlock extends Block
                        implements AnimatedBlock {

    public WaterBlock() {
        super("water_block", false, false);
    }

    @Override
    public String textureKey(int face) {
        return "textures/block/water_still.png";
    }
}
