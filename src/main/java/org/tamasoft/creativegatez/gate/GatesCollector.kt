package org.tamasoft.creativegatez.gate

import org.bukkit.Location
import org.bukkit.block.Block
import org.tamasoft.creativegatez.teleport.BlockLocation
import org.tamasoft.creativegatez.util.JsonFileUtil
import java.io.File
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.thread

object GatesCollector {

    val gates: MutableSet<Gate> = HashSet()

    open class LocationToGateMap {
        private val map : MutableMap<BlockLocation, Gate> = LinkedHashMap()
        operator fun get(startBlock : Block): Gate? {
            val blockLocation = BlockLocation.fromBlock(startBlock)
            return map[blockLocation]
        }
        operator fun get(location: Location): Gate? {
            val blockLocation = BlockLocation.fromLocation(location)
            return map[blockLocation]
        }
        operator fun set(location: BlockLocation, gate: Gate) {
            map[location] = gate
        }
        fun remove(location: BlockLocation) {
            map.remove(location)
        }
    }

    object Frames : LocationToGateMap()
    object Portals : LocationToGateMap()

    fun register(gate : Gate) {
        gate.frameCoords.forEach { Frames[it] = gate }
        gate.portalCoords.forEach { Portals[it] = gate }
        gates.add(gate)
    }

    fun remove(gate: Gate) {
        gate.frameCoords.forEach { Frames.remove(it) }
        gate.portalCoords.forEach { Portals.remove(it) }
        gates.remove(gate)
    }

    fun getUniqueGates(): List<Gate> {
        return gates.toList()
    }

    fun getByNetworkId(networkId: String): List<Gate> {
        val ret: MutableList<Gate> = ArrayList()
        gates.stream()
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