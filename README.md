![Logo](https://i.ibb.co/1T6n2st/IMG-0005-copy.png)

This plugin is inspired (and based on) the fantastic [Creative Gates](https://dev.bukkit.org/projects/creativegates) plugin by the Massive Craft group.

Unfortunately, that plugin has not received updates since Minecraft 1.12, hence this project.

This is in ALPHA version: that is to say, it's still under testing.

What Is It?
-----------

CreativeGatez allows you to create teleportation gates without running any commands.
* Create a gate frame of any form and with any material that contains two emerald blocks.
* Name a clock with an anvil.
* Hit the inside of the frame with the clock (right click).
* Any gate created with clocks named with the same name will be connected in a chain.


Install
-------

1.  Stop your server.
2.  Put CreativeGatez.jar in your plugins folder.
3.  Start your server again.
4.  Modify /CreativeGatez/config.json to customise the configuration (then restart the server).

Portal vs Water
---------------

CreativeGatez can use either portal blocks or water blocks for portal content. You decide which to use in config.json by changing the usingWater parameter.

**For nether-portal blocks** to work without bugs you must open your server.properties and set allow-nether=false. This will disable the generation of the vanilla nether map and the vanilla nether portals. You can add one, two or even three custom nether worlds using a world manager plugin later on.

**For water blocks** to work you don't need to do anything special. If you want the vanilla nether world and portals you probably want to change to water portals in the config. Otherwise players entering nether gates in the nether might exit through creative gates.

Development
-----------

[Source Code](https://github.com/marcotama/CreativeGatez)

Please submit bugs if you find problems.

Pull requests are welcome.