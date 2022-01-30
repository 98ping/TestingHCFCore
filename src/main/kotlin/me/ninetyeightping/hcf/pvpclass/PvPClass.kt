package me.ninetyeightping.hcf.pvpclass

import org.bukkit.entity.Player

abstract class PvPClass(var displayName: String, var type: PvPClassType) {

    abstract fun onEquip(player: Player)
    abstract fun hasArmorOn(player: Player) : Boolean
    abstract fun onRemoval(player: Player)


}