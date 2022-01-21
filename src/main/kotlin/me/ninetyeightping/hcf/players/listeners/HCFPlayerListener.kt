package me.ninetyeightping.hcf.players.listeners

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.InjectionUtil
import org.bukkit.Material
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

class HCFPlayerListener : Listener {

    @EventHandler
    fun projectileShoot(event: ProjectileLaunchEvent) {
        if (event.entity is EnderPearl) {
            if (event.entity.shooter is Player) {
                val player = event.entity.shooter as Player
                if (!HCF.instance.timerHandler.enderpearlTimer.hasCooldown(player)) {
                    HCF.instance.timerHandler.enderpearlTimer.addCooldown(player)
                }
            }
        }
    }

    @EventHandler
    fun projectileInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.itemInHand.type == Material.ENDER_PEARL) {
                if (HCF.instance.timerHandler.enderpearlTimer.hasCooldown(event.player)) {
                    event.isCancelled = true
                    event.player.sendMessage(Chat.format("&c&lCurrently on &9Enderpearl &c&lcooldown"))
                }
            }
        }
    }

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val player = event.player;

        if (InjectionUtil.get(HCFPlayerHandler::class.java).byPlayer(player) == null) {
            val hcfPlayer = HCFPlayer(player.uniqueId.toString(), player.name, 0.0, StatisticEntry(0, 0, 0, 0))
            InjectionUtil.get(HCFPlayerHandler::class.java).createPlayer(hcfPlayer)
        }
    }
}