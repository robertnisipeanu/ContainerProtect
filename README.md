# ContainerProtect
ContainerProtect is a minecraft(spigot) plugin that adds protections on your chests, similar to LWC, but it is a lot more lightweight and doesn't uses queries that blocks the main thread. Instead of using SQL (or any other type of database) it uses Spigot's 1.14 PersistentDataHolder API to store data directly on the containers (chests/furnaces/hoppers and any other block of which it's BlockState extends the [TileState](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/TileState.html) class).
