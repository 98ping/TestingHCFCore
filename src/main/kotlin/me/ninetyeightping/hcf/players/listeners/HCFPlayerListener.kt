package me.ninetyeightping.hcf.players.listeners

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import me.ninetyeightping.hcf.timers.impl.EnderpearlTimer
import me.ninetyeightping.hcf.util.Chat
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.concurrent.ThreadLocalRandom


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

        val hcfplayerForEntity = HCF.instance.playerHandler.byPlayer(player) ?: return

        hcfplayerForEntity.stats.deaths = (hcfplayerForEntity.stats.deaths + 1)
        if (hcfplayerForEntity.stats.killstreak > 0) {
            hcfplayerForEntity.stats.killstreak = 0
        }
        hcfplayerForEntity.push()

        val teamToReduceDTR = HCF.instance.teamHandler.byPlayer(player) ?: return

        teamToReduceDTR.registerPlayerDeath(player)

        val killer = event.entity.killer
        if (killer != null) {

            val hcfplayerForKiller = HCF.instance.playerHandler.byPlayer(killer)
            if (hcfplayerForKiller == null) {
                println("HCFPlayer for killer is null.")
                return
            }
            hcfplayerForKiller.stats.kills += 1
            hcfplayerForKiller.stats.killstreak += 1
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
    fun join(event: AsyncPlayerPreLoginEvent) {
        val hcfPlayer = HCF.instance.playerHandler.byUUID(event.uniqueId)

        if (hcfPlayer == null)
        {
            HCF.instance.playerHandler.createPlayer(HCFPlayer(event.uniqueId, event.name))
            return
        }

        HCF.instance.playerHandler.cache(hcfPlayer)
    }
}