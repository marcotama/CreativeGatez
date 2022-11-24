package org.tamasoft.creativegatez.gate

import org.bukkit.Location
import org.bukkit.block.Block
import org.tamasoft.creativegatez.teleport.BlockLocation
import org.tamasoft.creativegatez.util.JsonFileUtil
import java.io.File
import kotlin.concurrent.thread

object GatesCollector {

    val gates: MutableSet<Gate> = HashSet()

    open class WorldToLocationToGateMap {
        private val map : MutableMap<String, MutableMap<BlockLocation, Gate>> = LinkedHashMap()
        private operator fun get(world: String): MutableMap<BlockLocation, Gate> {
            return map.computeIfAbsent(world) { LinkedHashMap() }
        }
        operator fun get(startBlock : Block): Gate? {
            return this[startBlock.location]
        }
        operator fun get(location: Location): Gate? {
            val world = location.world.name
            val blockLocation = BlockLocation.fromLocation(location)
            return this[world][blockLocation]
        }
        operator fun set(world: String, location: BlockLocation, gate: Gate) {
            this[world][location] = gate
        }
        fun remove(world: String, location: BlockLocation) {
            this[world].remove(location)
        }
    }

    open class WorldToLocationToGatesMap {
        private val map : MutableMap<String, MutableMap<BlockLocation, MutableSet<Gate>>> = LinkedHashMap()
        operator fun get(startBlock : Block): MutableSet<Gate> {
            return this[startBlock.location]
        }
        operator fun get(location: Location): MutableSet<Gate> {
            return this[location.world.name, BlockLocation.fromLocation(location)]

        }
        private fun getRaw(world: String, location: BlockLocation): MutableSet<Gate> {
            return map
                .computeIfAbsent(world) { LinkedHashMap() }
                .computeIfAbsent(location) { LinkedHashSet() }

        }
        operator fun get(world: String, location: BlockLocation): MutableSet<Gate> {
            return getRaw(world, location).toMutableSet() // clone

        }
        fun add(world: String, location: BlockLocation, gate: Gate) {
            getRaw(world, location).add(gate)
        }
        fun remove(world: String, location: BlockLocation, gate: Gate) {
            getRaw(world, location).remove(gate)
        }
    }

    object Frames : WorldToLocationToGatesMap()
    object Portals : WorldToLocationToGateMap()

    fun register(gate : Gate) {
        gate.frameCoords.forEach { Frames.add(gate.exit.world, it, gate) }
        gate.portalCoords.forEach { Portals[gate.exit.world, it] = gate }
        gates.add(gate)
    }

    fun remove(gate: Gate) {
        gate.frameCoords.forEach { Frames.remove(gate.exit.world, it, gate) }
        gate.portalCoords.forEach { Portals.remove(gate.exit.world, it) }
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