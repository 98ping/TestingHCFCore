package me.ninetyeightping.hcf.timers.impl

import me.ninetyeightping.hcf.timers.Timer
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

object EnderpearlTimer : Timer() {

    val cooldownMap = hashMapOf<UUID, Long>()

    override fun hasCooldown(player: Player) : Boolean {
        return cooldownMap.containsKey(player.uniqueId) && System.currentTimeMillis() <= cooldownMap[player.uniqueId]!!
    }

    override fun addCooldown(player: Player) {
        val cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(16)
        cooldownMap[player.uniqueId] = cooldown
    }

    override fun removeCooldown(player: Player) {
        cooldownMap.remove(player.uniqueId)
    }
}