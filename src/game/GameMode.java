package game;

import utils.Input;
import world.World;

public interface GameMode {
    void onEnter(Player player);
    void onExit(Player player);
    void update(Player player, Input input, float dt);
}
