package me.ninetyeightping.hcf.timers.impl

import me.ninetyeightping.hcf.timers.Timer
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit

object FHomeTimer : Timer() {

    val startingLocationMap = hashMapOf<UUID, Location>()

    val cooldownMap = hashMapOf<UUID, Long>()

    override fun hasCooldown(player: Player) : Boolean {
        return cooldownMap.containsKey(player.uniqueId) && System.currentTimeMillis() <= cooldownMap[player.uniqueId]!!
    }

    override fun addCooldown(player: Player) {
        val cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(11)
        cooldownMap[player.uniqueId] = cooldown
        startingLocationMap[player.uniqueId] = player.location
    }

    override fun removeCooldown(player: Player) {
        cooldownMap.remove(player.uniqueId)
        startingLocationMap.remove(player.uniqueId)
    }
}