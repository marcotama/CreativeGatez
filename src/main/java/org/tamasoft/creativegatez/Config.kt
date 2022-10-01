package org.tamasoft.creativegatez

import org.bukkit.Material
import java.util.*

class Config {
    val teleportationSoundActive = true
    val usingWater = false
    val pigmanPortalSpawnAllowed = true
    val maxArea = 200
    val removingCreateToolName = true
    val removingCreateToolItem = false
    val blocksRequired: Map<Material, Long> = mapOf(Material.EMERALD_BLOCK to 2)
    val materialCreate: Material = Material.CLOCK
    val materialInspect = Material.BLAZE_POWDER
    val materialSecret = Material.MAGMA_CREAM
    val materialMode = Material.BLAZE_ROD
    val voidMaterials: Set<Material> = EnumSet.of(Material.AIR)
}