# Crappy Minecraft - TODO List

## Next up
- [ ] Implement Item, Inventory, and their rendering. (inventory is not in the HUD)
  - HUD is what is perma rendered on the screen (crosshair, hotbar, health, armor, ...)

## Optimisations
- [x] Change biomeMap texture: upload texture once to GPU at chunk creation, store texID in chunk and bind to it before rendering
- [ ] Implement LODs (Distant Horizons type shit)
- [x] Pack data in fewer bits
  - x, y, z, faceIdx, cornerIdx, textureID in a 32-bit integer
  - if flags needed (torchlight, LOD level, ...) 1 more attrib needed probably a byte is enough
- [x] Frustum culling
- [ ] Meshing techniques (greedy meshing, ...) ??

## Graphics
### General
- [ ] Fill all the addFace() methods in the ChunkMesher (different data layout for water and solid)
- [x] LUT texture for tints
- [x] Texture of biomeID for each chunk
- [x] Add a player model (with skin support ?)
  - [ ] Add multiple camera support (only render player model if in 3rd person)
- [ ] Water texture animation
  - [ ] Different shader for default water and realistic water
- [ ] Lighting
  - Torch lighting, other light sources ??
  - Better ambient lighting
  - Remove this ugly ass directional lighting
- [ ] Distance fog 
- [ ] Sky box
- [x] Bi-layered textured (Ex: for all types of grass blocks) → there are 6 flag bits to assign biome, ... to a vertex
### GUI
- [x] Targeted block outline
- [ ] Hotbar
- [ ] Inventory
- [ ] Crosshair

## Terrain
- [x] Perlin noise
- [x] Underwater fog effects
- [x] Biome support
- [ ] Generate terrain differently per biome minecraft-like: (cfr. https://www.youtube.com/watch?v=CSa5O6knuwI)
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

## Audio
- [ ] Play a background music (per biome ?)
- [ ] Special sound per block when placed / broken
- [ ] Play some sfx when clicking buttons in menus

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
- [x] In-game settings panel
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