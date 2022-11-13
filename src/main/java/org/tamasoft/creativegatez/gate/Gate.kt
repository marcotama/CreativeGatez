package org.tamasoft.creativegatez.gate

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Orientable
import org.bukkit.entity.Player
import org.tamasoft.creativegatez.CreativeGatez
import org.tamasoft.creativegatez.State
import org.tamasoft.creativegatez.teleport.BlockLocation
import org.tamasoft.creativegatez.teleport.Destination
import org.tamasoft.creativegatez.teleport.TeleporterException
import org.tamasoft.creativegatez.util.SmokeUtil
import org.tamasoft.creativegatez.util.TeleportUtil
import org.tamasoft.creativegatez.util.TxtUtil
import org.tamasoft.creativegatez.util.VoidUtil
import java.util.*
import kotlin.collections.ArrayList

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class Gate() {

    lateinit var networkId: String
    lateinit var exit: Destination
    lateinit var creatorId: UUID
    var creationTimeMillis: Long = 0
    var id: Int = 0

    constructor(networkId: String, exit: Destination, coords: Set<BlockLocation>, creatorId: UUID) : this() {
        this.networkId = networkId
        this.exit = exit
        this.coords = coords
        this.creatorId = creatorId
        this.creationTimeMillis = System.currentTimeMillis()
    }


    fun calcId() : Int {
        val builder = HashCodeBuilder()
        coords.stream()
            .sorted(Comparator.comparingInt(BlockLocation::blockX)
                .thenComparingInt(BlockLocation::blockY)
                .thenComparingInt(BlockLocation::blockZ)
            )
            .forEach(builder::append)
        return builder.hashCode()
    }


    var restricted = false
    var enterEnabled = true
    var exitEnabled = true
    var coords: Set<BlockLocation> = HashSet()
        get() {
            return Collections.unmodifiableSet(field)
        }
        set(coords) {
            field = HashSet(coords)
            id = calcId()
        }

    fun isCreator(sender: Player): Boolean {
        return sender.identity().uuid() == creatorId
    }

    fun destroy() {
        GatesCollector.remove(this)
        empty()
        fxKitDestroy()
    }

    fun toggleMode() {
        val enter = enterEnabled
        val exit = exitEnabled
        if (enter) {
            if (exit) {
                enterEnabled = false
                exitEnabled = false
            } else {
                enterEnabled = false
                exitEnabled = true
            }
        } else {
            if (exit) {
                enterEnabled = true
                exitEnabled = true
            } else {
                enterEnabled = true
                exitEnabled = false
            }
        }
    }

    fun transport(player: Player) {
        val gateChain = calcGatesInChainAfterThis()
        var message: String?
        for (gate in gateChain) {
            if (!gate!!.exitEnabled) continue
            val network = GatesCollector.getByNetworkId(gate.networkId)
            val destination = network[(network.indexOf(gate) + 1) % network.size]
            try {
                TeleportUtil.teleport(player, destination.exit)
                fxKitUse()
                return
            } catch (e: TeleporterException) {
                message = e.message
                if (message != null) {
                    player.sendMessage(message)
                }
            }
        }
        message = TxtUtil.parse("<i>This gate does not seem to lead anywhere.")
        player.sendMessage(message)
    }

    fun calcGatesInChainAfterThis() : List<Gate?> {
        val rawChain = GatesCollector.getByNetworkId(networkId)
        val myIndex = rawChain.indexOf(this)
        if (myIndex >= 0) {
            val ret: MutableList<Gate?> = ArrayList()

            // Add what is after me
            ret.addAll(rawChain.subList(myIndex + 1, rawChain.size))

            // Add what is before me
            ret.addAll(rawChain.subList(0, myIndex))

            return ret
        } else {
            throw IllegalStateException("The gates chain in gate $id does not contain the gate itself")
        }
    }

    // These blocks are sorted since the coords are sorted
    val blocks: List<Block>?
        @JsonIgnore
        get() {
            val ret: MutableList<Block> = ArrayList()
            val world: World = try {
                exit.getBukkitWorld()
            } catch (e: IllegalStateException) {
                return null
            }
            for (coord in coords) {
                val bx = coord.blockX
                val by = coord.blockY
                val bz = coord.blockZ
                val block = world.getBlockAt(bx, by, bz)
                ret.add(block)
            }
            return ret
        }
    val centerBlock: Block?
        @JsonIgnore
        get() {
            val blocks = blocks ?: return null
            return blocks[blocks.size / 2]
        }
    val isIntact: Boolean
        @JsonIgnore
        get() {
            val blocks = blocks ?: return true
            for (block in blocks) {
                if (VoidUtil.isVoid(block)) {
                    return false
                }
            }
            return true
        }

    fun setContent(material: Material) {
        val blocks = blocks ?: return
        var axis: Axis = Axis.X

        // Orientation check
        if (material == Material.NETHER_PORTAL) {
            val origin = blocks[0]
            val blockSouth = origin.getRelative(BlockFace.SOUTH)
            val blockNorth = origin.getRelative(BlockFace.NORTH)
            if (blocks.contains(blockNorth) || blocks.contains(blockSouth)) {
                axis = Axis.Z
            }
        }
        for (block in blocks) {
            val blockMaterial = block.type
            if (blockMaterial != Material.NETHER_PORTAL
                && blockMaterial != Material.WATER
                && !VoidUtil.isVoid(blockMaterial)
            ) continue
            block.type = material

            // Apply orientation
            if (material != Material.NETHER_PORTAL) continue
            val orientable = block.blockData as Orientable
            orientable.axis = axis
            block.blockData = orientable
        }
    }

    fun fill() {
        State.isFilling = true
        setContent(if (CreativeGatez.configuration.usingWater) Material.WATER else Material.NETHER_PORTAL)
        State.isFilling = false
    }

    fun empty() {
        setContent(Material.AIR)
    }

    fun fxKitCreate() {
        fxShootSound()
    }

    fun fxKitUse() {
        if (!CreativeGatez.configuration.teleportationSoundActive) return
        fxShootSound()
    }

    fun fxKitDestroy() {
        fxExplode()
    }

    fun fxExplode() {
        val block = centerBlock ?: return
        val location = block.location
        SmokeUtil.fakeExplosion(location)
    }

    fun fxShootSound() {
        val block = centerBlock ?: return
        val location = block.location
        location.world?.playEffect(location, Effect.GHAST_SHOOT, 0)
    }
}