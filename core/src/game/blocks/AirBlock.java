package game.blocks;

public class AirBlock extends Block {

    public AirBlock() {
        super("air_block", false, true);
    }

    @Override
    public String textureKey(int face) {
        return "textures/particle/big_smoke_11.png";
    }
}
