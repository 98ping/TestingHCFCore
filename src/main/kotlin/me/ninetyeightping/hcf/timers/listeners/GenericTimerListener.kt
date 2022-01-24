package me.ninetyeightping.hcf.timers.listeners

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.timers.impl.CombatTimer
import me.ninetyeightping.hcf.timers.impl.FHomeTimer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerMoveEvent

class GenericTimerListener : Listener {

    @EventHandler
    fun move(event: PlayerMoveEvent) {
        val player = event.player
        if (FHomeTimer.startingLocationMap.containsKey(player.uniqueId) && FHomeTimer.hasCooldown(player)) {
            val location = FHomeTimer.startingLocationMap[player.uniqueId]

            if (player.location.x != location!!.x || player.location.y != location.y || player.location.z != location.z) {
                FHomeTimer.removeCooldown(player)
                FHomeTimer.startingLocationMap.remove(player.uniqueId)
            }
        }


    }

    @EventHandler
    fun damage(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            val damager = event.damager as Player
            val player = event.entity as Player

            CombatTimer.addCooldown(damager)
            CombatTimer.addCooldown(player)
        }
    }
}