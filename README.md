![Logo](https://i.ibb.co/1T6n2st/IMG-0005-copy.png)

This plugin is inspired (and based on) the fantastic [Creative Gates](https://dev.bukkit.org/projects/creativegates) plugin by the [Massive Craft](https://www.massivecraft.com) group.

Unfortunately, that plugin has not received updates since Minecraft 1.12, hence this project.

This is in ALPHA version: that is to say, it's still under testing.

What Is It?
-----------

CreativeGatez allows you to create teleportation gates without running any commands.

Usage
-----

You can create gates, but you can also change some settings. Here's how you do it.

### Create a Gate

*   Create a gate frame of any form and with any material that contains 2 (configurable) emerald blocks (configurable).
*   Name a clock (configurable) with an anvil.
*   Hit the inside of the frame with the clock (right click).
*   Any gate created with clocks named with the same name will be connected in a chain.

### Inspect a Gate

*   Use blaze powder (configurable) against a gate.
*   Read in the chat the network name (the name of the clock used to create the gate) and the number of gates in the network.

### Make Gate Info Secret

*   Use magma cream (configurable) against the gate.
*   Now whether everyone or only you can get information when inspecting a gate is toggled.

### Enable/Disable Gate Entry/Exit

*   Use a blaze rod (configurable) against the gate.
*   The gate will toggle between
    *   entry: enabled, exit: enabled
    *   entry: disabled, exit: disabled
    *   entry: enabled, exit: disabled
    *   entry: disabled, exit: enabled

Install
-------

1.  Stop your server.
2.  Put CreativeGatez.jar in your plugins folder.
3.  Start your server again.
4.  Modify /CreativeGatez/config.json to customise the configuration (then restart the server).

Portal vs Water
---------------

CreativeGatez can use either portal blocks or water blocks for the portal content. You decide which to use in _config.json_ by changing the usingWater parameter.

**For nether-portal blocks** to work without bugs you must open your server.properties and set allow-nether=false. This will disable the generation of the vanilla nether map and the vanilla nether portals. You can add nether worlds using a world manager plugin anyway.

**For water blocks** to work you don't need to do anything special. If you want the vanilla nether world and portals you probably want to change to water portals in the config. Otherwise, players entering nether gates in the nether might exit through portal gates.

If, for any reason, water flows from portals, block it with any block (e.g. dirt) and then remove the blocks you added.

Development
-----------

[Source Code](https://github.com/marcotama/CreativeGatez)

Please submit bugs if you find problems.

Pull requests are welcome.