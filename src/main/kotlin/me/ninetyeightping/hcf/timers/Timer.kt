package me.ninetyeightping.hcf.timers

import org.bukkit.entity.Player

abstract class Timer {


    abstract fun hasCooldown(player: Player) : Boolean
    abstract fun addCooldown(player: Player)
    abstract fun removeCooldown(player: Player)

}