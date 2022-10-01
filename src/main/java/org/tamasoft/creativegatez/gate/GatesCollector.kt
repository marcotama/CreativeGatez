package org.tamasoft.creativegatez.gate

import org.bukkit.Location
import org.bukkit.block.Block
import org.tamasoft.creativegatez.teleport.BlockLocation
import org.tamasoft.creativegatez.util.JsonFileUtil
import java.io.File
import java.util.*
import java.util.stream.Collectors
import kotlin.concurrent.thread

object GatesCollector {

    private val gates : MutableMap<BlockLocation, Gate> = LinkedHashMap()

    operator fun get(startBlock : Block): Gate? {
        val blockLocation = BlockLocation.fromBlock(startBlock)
        return gates[blockLocation]
    }
    operator fun get(location: Location): Gate? {
        val blockLocation = BlockLocation.fromLocation(location)
        return gates[blockLocation]
    }

    fun register(gate : Gate) {
        gate.coords.forEach { gates[it] = gate }

    }

    fun remove(gate: Gate) {
        gate.coords.forEach { gates.remove(it) }
    }

    fun getUniqueGates(): List<Gate> {
        return gates.values.stream()
            .distinct()
            .collect(Collectors.toList())
    }

    fun getByNetworkId(networkId: String): List<Gate> {
        val ret: MutableList<Gate> = ArrayList()
        gates.values.stream()
            .filter { g : Gate -> g.networkId == networkId }
            .distinct()
            .sorted(Comparator.comparingLong(Gate::creationTimeMillis))
            .forEach(ret::add)
        return ret
    }

    fun loadFromFile(gatesFile: File) {
        if (gatesFile.exists()) {
            val gateList = JsonFileUtil.read(gatesFile, GateList::class.java)
            gateList.gates.forEach { register(it) }
        } else {
            val gateList = GateList(getUniqueGates())
            JsonFileUtil.write(gatesFile, gateList)
        }
    }

    fun saveGates(gatesFile: File) {
        thread(start = true) {
            val gateList = GateList(getUniqueGates())
            JsonFileUtil.write(gatesFile, gateList)
        }
    }
}