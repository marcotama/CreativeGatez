package org.tamasoft.creativegatez.util

import org.bukkit.Material
import org.bukkit.block.Block
import org.tamasoft.creativegatez.CreativeGatez

object VoidUtil {
    fun isVoid(material: Material): Boolean {
        return CreativeGatez.configuration.voidMaterials.contains(material)
    }

    fun isVoid(block: Block): Boolean {
        return isVoid(block.type)
    }
}