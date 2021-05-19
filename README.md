# ContainerProtect
ContainerProtect is a minecraft(spigot) plugin that adds protections on your chests (or other blocks with inventories).

ContainerProtect is similar to LWC, but it is a lot more lightweight and doesn't uses queries that blocks the main thread. 
Instead of using SQL (or any other type of database) it uses Spigot's 1.14 PersistentDataHolder API to store data directly on the 
containers (chests/furnaces/hoppers and any other block of which it's BlockState extends the 
[TileState](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/TileState.html) class). Unlike LWC, 
this results in "queries" that aren't noticeable at all (or mostly at all) as the server will load the data with the block 
(and that usually happens when the chunk loads), loading the data just as it would load the items stored inside the chest.

Because the plugin uses PersistentDataHolder API, it can only protect a specific list of blocks (everything extending TileState). 
In the future, support for entities will be added to the plugin (e.g. Villagers, Minecarts with Chest etc.)

### Supported blocks
The plugin uses a method called reflection to get everything extending TileState, meaning if any future minecraft version adds 
another block that extends TileState, the plugin will pick that up automatically and append it to your existent config.

Known and supported blocks for Minecraft 1.16.5:
- Banner, Barrel, Beacon, Bed, Beehive, Bell, BlastFurnace, BrewingStand, Campfire, Chest(+Trapped Chest), CommandBlock, 
  Comparator, Conduit, CreatureSpawner, DaylightDetector, Dispenser, Dropper, EnchantingTable, EnderChest, EndGateway, 
  Furnace, Hopper, Jigsaw, Jukebox, Lectern, ShulkerBox, Sign, Skull, Smoker, Structure

## Installation
1. Download the .jar and drop it inside your `plugins` directory then restart the server. 
2. Start the server 
3. The plugin generates a config file inside it's data directory (`plugins/ContainerProtect`). 
   Modify this config file to edit plugin settings.
4. After modifying plugin config, execute the `/creload` command to reload the config file.
5. You are good to go. Please report any bugs you find as an issue on GitHub (preferably with steps to reproduce).

## Commands & Permissions
### Commands
- `/cinfo` - Enter container info mode, click on a container to see information about it 
  (who is the owner and who is allowed to access it)
  - Permission: `containerprotect.info`
- `/cprivate` - Enter container private mode, click on a container to register it on your name 
  (it will fail if the container is already registered by someone else)
  - Permission: `containerprotect.private`
- `/cmodify <PlayerName>` - Enter container modify mode, click on the container (needs to be registered by you) 
  to allow someone else to access it
  - `<PlayerName>` - The name of the player you want to give access to
  - Permission: `containerprotect.modify`
- `/cremove` - Enter container remove mode, click on a container to unregister it (needs to be registered by you)
  - Permission: 
    - Players: `containerprotect.remove` (can only remove own protections)
    - Admins: `containerprotect.admin.remove` (can remove any protection)
- `/creload` - Reload config file
  - Permission: `containerprotect.admin.reload`
    
### Permissions
- Players:
  - `containerprotect.info` - Access to /cinfo command
  - `containerprotect.private` - Access to /cprivate command
  - `containerprotect.modify` - Access to /cmodify command
  - `containerprotect.remove` - Access to /cremove command
- Admins:
  - `containerprotect.admin.reload` - Access to /creload command
  - `containerprotect.admin.remove` - Access to /cremove command on any container

## Contributing
If you want to contribute, please follow those basic guidelines:
- If it's a bug fix that doesn't change the normal behaviour of the plugin, you can open a PR directly with the fix
- For any other contribution, please first open an issue, so we can discuss it and then, 
  only after we decide on how we are going to approach it you can proceed to open a PR for it

### Bug reporting/Feature requests
To report any bug, please report an issue describing it and how to reproduce it (or at least what you did before it happened). 
Images/Videos are appreciated.

To propose a new feature, just open an issue describing the feature in your best words.