package org.tamasoft.creativegatez

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.tamasoft.creativegatez.gate.GatesCollector
import org.tamasoft.creativegatez.util.JsonFileUtil
import java.io.File
import java.nio.file.Files
import java.util.logging.Logger
import kotlin.concurrent.fixedRateTimer

class CreativeGatez : JavaPlugin(), Listener {

    val configFile = File(dataFolder, CONFIG_FILE_NAME)
    val gatesFile = File(dataFolder, GATES_FILE_NAME)
    init {
        Files.createDirectories(dataFolder.toPath())
    }

    companion object {
        var instance: CreativeGatez? = null
        lateinit var configuration: Config
        private const val CONFIG_FILE_NAME = "config.json"
        private const val GATES_FILE_NAME = "gates.json"

    }

    @get:JvmName("getLogger1")
    val logger: Logger = Bukkit.getLogger()

    override fun onLoad() {
        instance = this
        if (configFile.exists()) {
            configuration = JsonFileUtil.read(configFile, Config::class.java)
        } else {
            configuration = Config()
            JsonFileUtil.write(configFile, configuration)
        }
        GatesCollector.loadFromFile(gatesFile)
    }

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this)
        server.pluginManager.registerEvents(EngineMain(), this)
        fixedRateTimer("saveGates", false, 0L, 60 * 1000) {
            GatesCollector.saveGates(gatesFile)
        }
        logger.info { "Enabled" }
    }

    override fun onDisable() {
        GatesCollector.saveGates(gatesFile)
        logger.info { "Disabled" }
    }
}