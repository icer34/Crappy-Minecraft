# Crappy Minecraft - TODO List

## Optimisations
- [ ] Implement LODs (Distant Horizons type shit)
- [x] Pack data in fewer bits
  - x, y, z, faceIdx, cornerIdx, textureID in a 32-bit integer
  - if flags needed (torchlight, LOD level, ...) 1 more attrib needed probably a byte is enough
- [x] Frustum culling
- [ ] Meshing techniques (greedy meshing, ...) ??

## Graphics
- [ ] Water texture animation
- [x] Moving water surface
- [ ] Lighting
  - Torch lighting, other light sources ??
  - Better ambient lighting
  - Remove this ugly ass directional lighting
- [ ] Distance fog 

## Terrain
- [x] Perlin noise
- [x] Underwater fog effects
- [ ] Biomes, generate terrain differently per biome minecraft-like: (cfr. https://www.youtube.com/watch?v=CSa5O6knuwI)
  - Continentalness and other indices
  - Heatmap for humidity
  - ...
- [ ] Surface features: 
  - Trees
  - Grass (tall ?), flowers, ...
  - Lakes
  - Other structures (small houses, ...) ??
  - ...
- [ ] Caves
  - Cave features ?

## Launcher
- [ ] Supports settings :
  - Keybinds
  - Graphic settings
  - Cpu usage (low - medium - high - extreme)
  - Memory usage (how much RAM you allocate to the game)
- [ ] Supports multiple worlds that can be saved / loaded from the launcher
  - Needs data format to encode a world data (seed, modified chunks with the modifications, ...)
- Networking features: 
  - [ ] Supports login / register to access servers
- [ ] World creation menu
  - Change seed, game mode, mobs AI settings, name, ...

## Networking
- [ ] Launcher acts like a client for multiplayer worlds
  - needs a server hosting the world and sends modifications to the client
- [ ] Player sign-in / register account to access servers

## Game Features
- [ ] Redstone
- [ ] In game chat with commands
  - Permission level system allowing restrictions to certain op commands (tp, game mode, ...)
- [ ] Multiple dimensions
- [ ] Special hitbox blocks
  - doors, beds, fences, slabs, ...
- [ ] Entities
  - Oumar Sow : 
    - If you have any merguez in your inventory, he attacks else he leaves you alone
    - Only spawns in Oumar biomes in which merguez fall from trees
  - Naingui : 
    - Is attracted to Oumar Sow's