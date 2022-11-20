package org.tamasoft.creativegatez.util

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.tamasoft.creativegatez.CreativeGatez
import org.tamasoft.creativegatez.gate.GateOrientation

object FloodUtil {
    /**
     * Given a void block, finds the connected void blocks as well as a frame of non-void blocks surrounding it.
     * If the given block is non-void, returns null
     */
    fun getGateFloodInfo(startBlock: Block): FloodInfo? {

        // Search for content WE and NS
        val gateOrientation: GateOrientation
        var shiftedStartingBlock: Block = startBlock.getRelative(BlockFace.NORTH)
        val blocksNSN = FloodCalculator(GateOrientation.NS, CreativeGatez.configuration.maxArea)
            .calcFloodBlocks(shiftedStartingBlock)
            .getFoundBlocks()
        shiftedStartingBlock = startBlock.getRelative(BlockFace.SOUTH)
        val blocksNSS = FloodCalculator(GateOrientation.NS, CreativeGatez.configuration.maxArea)
            .calcFloodBlocks(shiftedStartingBlock)
            .getFoundBlocks()
        shiftedStartingBlock = startBlock.getRelative(BlockFace.EAST)
        val blocksWEE = FloodCalculator(GateOrientation.WE, CreativeGatez.configuration.maxArea)
            .calcFloodBlocks(shiftedStartingBlock)
            .getFoundBlocks()
        shiftedStartingBlock = startBlock.getRelative(BlockFace.WEST)
        val blocksWEW = FloodCalculator(GateOrientation.WE, CreativeGatez.configuration.maxArea)
            .calcFloodBlocks(shiftedStartingBlock)
            .getFoundBlocks()
        
        var found = 0
        if (blocksNSN != null) found++
        if (blocksNSS != null) found++
        if (blocksWEE != null) found++
        if (blocksWEW != null) found++
        if (found != 1) {
            return null
        }

        val portalBlocks: Set<Block>?
        if (blocksNSN != null) {
            portalBlocks = blocksNSN
            gateOrientation = GateOrientation.NS
        } else if (blocksNSS != null) {
            portalBlocks = blocksNSS
            gateOrientation = GateOrientation.NS
        } else if (blocksWEE != null) {
            portalBlocks = blocksWEE
            gateOrientation = GateOrientation.WE
        } else if (blocksWEW != null) {
            portalBlocks = blocksWEW
            gateOrientation = GateOrientation.WE
        } else {
            return null
        }

        // Add in the frame as well
        val expandedBlocks = expandedByOne(portalBlocks, gateOrientation.expandFaces)
        val frameBlocks = SetUtils.diff(expandedBlocks, portalBlocks)
        return FloodInfo(gateOrientation, portalBlocks, frameBlocks)
    }

    /**
     * Given a set of blocks, expands the set by adding all adjacent blocks reachable by one step in the given
     * directions.
     */
    private fun expandedByOne(blocks: Set<Block>, expandFaces: Set<BlockFace>): Set<Block> {
        val ret: MutableSet<Block> = HashSet(blocks)
        for (block in blocks) {
            for (face in expandFaces) {
                val potentialBlock = block.getRelative(face)
                if (ret.contains(potentialBlock)) continue
                ret.add(potentialBlock)
            }
        }
        return ret
    }

    class FloodInfo(
        var gateOrientation: GateOrientation,
        var portalBlocks: Set<Block>,
        var frameBlocks: Set<Block>
    )

    /**
     * Given a block, recursively walks void adjacent blocks and returns the set with all of them, or null if the area
     * is too large.
     */
    private class FloodCalculator(
        orientation: GateOrientation,
        maxArea: Int
    ) {
        private val foundBlocks: MutableSet<Block> = HashSet()
        private val expandFaces: Set<BlockFace>
        private val maxArea: Int
        private var aborted = false
        fun getFoundBlocks(): Set<Block>? {
            return if (aborted || foundBlocks.isEmpty()) {
                null
            } else {
                foundBlocks
            }
        }

        fun calcFloodBlocks(startBlock: Block): FloodCalculator {
            // Too large, abort
            if (foundBlocks.size > maxArea) {
                if (!aborted) {
                    aborted = true
                }
                return this
            }

            // Already analysed this one
            if (foundBlocks.contains(startBlock)) {
                return this
            }

            // Found void, find its void neighbours
            if (VoidUtil.isVoid(startBlock)) {
                foundBlocks.add(startBlock)
                for (face in expandFaces) {
                    val potentialBlock = startBlock.getRelative(face)
                    calcFloodBlocks(potentialBlock)
                }
            }
            return this
        }

        init {
            expandFaces = orientation.expandFaces
            this.maxArea = maxArea
        }
    }
}