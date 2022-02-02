package me.ninetyeightping.hcf.players.listeners

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.timers.impl.EnderpearlTimer
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.InjectionUtil
import org.bukkit.Material
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

class HCFPlayerListener : Listener {

    @EventHandler
    fun projectileShoot(event: ProjectileLaunchEvent) {
        if (event.entity is EnderPearl) {
            if (event.entity.shooter is Player) {
                val player = event.entity.shooter as Player
                if (!EnderpearlTimer.hasCooldown(player)) {
                    EnderpearlTimer.addCooldown(player)
                }
            }
        }
    }

    @EventHandler
    fun die(event: PlayerDeathEvent) {
        val player = event.entity

        val hcfplayerForEntity = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayer(player) ?: return

        hcfplayerForEntity.stats.deaths = (hcfplayerForEntity.stats.deaths + 1)
        if (hcfplayerForEntity.stats.killstreak > 0) {
            hcfplayerForEntity.stats.killstreak = 0
        }
        hcfplayerForEntity.push()

        val teamToReduceDTR = InjectionUtil.get(TeamHandler::class.java).byPlayer(player) ?: return

        teamToReduceDTR.registerPlayerDeath(player)

        val killer = event.entity.killer
        if (killer != null) {

            val hcfplayerForKiller = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayer(killer) ?: return
            hcfplayerForKiller.stats.kills = hcfplayerForKiller.stats.kills + 1
            hcfplayerForKiller.stats.killstreak = hcfplayerForKiller.stats.killstreak + 1
            hcfplayerForKiller.push()
        }
    }

    @EventHandler
    fun projectileInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.itemInHand.type == Material.ENDER_PEARL) {
                if (EnderpearlTimer.hasCooldown(event.player)) {
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