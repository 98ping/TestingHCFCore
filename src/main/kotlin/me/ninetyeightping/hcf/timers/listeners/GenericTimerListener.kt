package me.ninetyeightping.hcf.timers.listeners

import me.ninetyeightping.hcf.HCF
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class GenericTimerListener : Listener {

    @EventHandler
    fun damage(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            val damager = event.damager as Player
            val player = event.entity as Player

            HCF.instance.timerHandler.combatTimer.addCooldown(damager)
            HCF.instance.timerHandler.combatTimer.addCooldown(player)
        }
    }
}