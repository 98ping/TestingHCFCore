package me.ninetyeightping.hcf.events.sotw.listeners

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.events.sotw.SOTWHandler
import me.ninetyeightping.hcf.util.Chat
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class SOTWDamageListener : Listener {

    @EventHandler
    fun damage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            val player = event.entity as Player
            val damager = event.damager as Player

            if (HCF.instance.sotwHandler.serverIsOnSOTWTimer()) {
                if (!HCF.instance.sotwHandler.isSOTWEnabled(player) && HCF.instance.sotwHandler.isSOTWEnabled(damager)) {
                    damager.sendMessage(Chat.format("&cYou cannot damage non &a&lSOTW Enabled &cusers"))
                    event.isCancelled = true
                } else if (HCF.instance.sotwHandler.isSOTWEnabled(player) && !HCF.instance.sotwHandler.isSOTWEnabled(damager)) {
                    damager.sendMessage(Chat.format("&cYou cannot damage non &a&lSOTW Enabled &cusers"))
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun entityDamageEvent(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (HCF.instance.sotwHandler.serverIsOnSOTWTimer() && !HCF.instance.sotwHandler.isSOTWEnabled(player)) {
                event.isCancelled = true
            }
        }
    }
}