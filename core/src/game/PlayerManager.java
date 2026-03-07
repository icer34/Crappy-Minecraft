package game;

import utils.Input;
import world.World;

public class PlayerManager {
    private GameMode currentMode;
    private boolean spawnReady = false;

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

        if (!spawnReady) {
            if (!world.isChunkLoaded(player.getPos().x, player.getPos().z)) {
                // Chunk pas encore chargé : on gèle le joueur à la bonne hauteur
                player.setYPos(world.getGroundHeight(player.getPos().x, player.getPos().z));
                player.setYVel(0.0f);
                return;
            }
            spawnReady = true;
        }

        currentMode.update(player, input, dt);
    }
}
