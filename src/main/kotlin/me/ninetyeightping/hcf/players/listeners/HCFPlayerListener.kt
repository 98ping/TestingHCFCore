package me.ninetyeightping.hcf.players.listeners

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class HCFPlayerListener : Listener {

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val player = event.player;

        if (HCF.instance.hcfPlayerHandler.byPlayer(player) == null) {
            val hcfPlayer = HCFPlayer(player.uniqueId.toString(), player.name, 0.0, "")
            HCF.instance.hcfPlayerHandler.createPlayer(hcfPlayer)
        }
    }
}