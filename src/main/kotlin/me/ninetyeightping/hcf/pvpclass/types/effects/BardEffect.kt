package me.ninetyeightping.hcf.pvpclass.types.effects

import com.google.common.collect.HashBasedTable
import me.ninetyeightping.hcf.pvpclass.types.Bard
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect

data class BardEffect(
    var energy: Int,
    var potionEffect: PotionEffect
) {


    fun apply(player: Player) {
        Bard.applyBardEffect(player, potionEffect, energy)
    }


}