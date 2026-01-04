package game;

import utils.Input;
import world.World;

public class PlayerManager {
    private GameMode currentMode;

    public void setMode(Player player, GameMode mode) {
        if(currentMode != null) currentMode.onExit(player);
        currentMode = mode;
        currentMode.onEnter(player);
    }

    public GameMode getGameMode() {
        return currentMode;
    }

    public void update(Player player, World world, Input input, boolean inputEnabled, float dt) {
        if(!inputEnabled) return;
        currentMode.update(player, input, dt);
    }
}
