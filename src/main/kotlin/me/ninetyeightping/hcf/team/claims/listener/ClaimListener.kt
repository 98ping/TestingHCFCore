package me.ninetyeightping.hcf.team.claims.listener

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.team.claims.LandBoard
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.Cuboid
import me.ninetyeightping.hcf.util.InjectionUtil
import me.ninetyeightping.hcf.util.ItemBuilder
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

class ClaimListener : Listener {

    companion object {
        val claimWand = ItemBuilder.of(Material.GOLD_HOE).name("&cClaiming Wand").setUnbreakable(true).build()
    }

    @EventHandler
    fun tryAndPlaceInClaim(event: BlockPlaceEvent) {
        val location = event.block.location

        if (InjectionUtil.get(LandBoard::class.java).claimByLocation(location) != null) {
            if (event.player.gameMode != GameMode.CREATIVE) {
                val claim = InjectionUtil.get(LandBoard::class.java).claimByLocation(location)
                val team = InjectionUtil.get(LandBoard::class.java).teamByClaim(claim!!)

                if (!team!!.isMember(event.player)) {
                    event.isCancelled = true
                    event.player.sendMessage(Chat.format("&eYou cannot place blocks inside of " + team.color + team.fakeName + "&e's claim"))
                }
            }
        }
    }

    @EventHandler
    fun tryAndBreakInClaim(event: BlockBreakEvent) {
        val location = event.block.location

        if (InjectionUtil.get(LandBoard::class.java).claimByLocation(location) != null) {
            if (event.player.gameMode != GameMode.CREATIVE) {
                val claim = InjectionUtil.get(LandBoard::class.java).claimByLocation(location)
                val team = InjectionUtil.get(LandBoard::class.java).teamByClaim(claim!!)

                if (!team!!.isMember(event.player)) {
                    event.isCancelled = true
                    event.player.sendMessage(Chat.format("&eYou cannot break blocks inside of " + team.color + team.fakeName + "&e's claim"))
                }
            }
        }
    }


    @EventHandler
    fun interact(event: PlayerInteractEvent) {
        val player = event.player;

        if (player.itemInHand.isSimilar(claimWand)) {
            val team = InjectionUtil.get(TeamHandler::class.java).byPlayer(player)
            if (team == null) return
            if (event.action == Action.LEFT_CLICK_BLOCK) {

                event.isCancelled = true

                var claimSession = HCF.instance.landBoard.sessions.getOrDefault(player.uniqueId, null)
                if (claimSession == null) return

                claimSession.position1 = event.clickedBlock.location
                player.sendMessage(Chat.format("&aUpdated position 1"))


            }
            if (event.action == Action.RIGHT_CLICK_BLOCK) {

                event.isCancelled = true

                var claimSession = HCF.instance.landBoard.sessions.getOrDefault(player.uniqueId, null)
                if (claimSession == null) return

                claimSession.position2 = event.clickedBlock.location
                player.sendMessage(Chat.format("&aUpdated position 2"))


            }
            if (event.action == Action.RIGHT_CLICK_AIR && player.isSneaking) {

                event.isCancelled = true

                var claimSession = HCF.instance.landBoard.sessions.getOrDefault(player.uniqueId, null)
                if (claimSession == null) return

                if (claimSession.position1 != null && claimSession.position2 != null) {


                    val loc1 = claimSession.position1!!.clone()
                    loc1.y = 256.0;

                    val loc2 = claimSession.position2!!.clone()
                    loc2.y = 0.0;

                    if (loc1.world != loc2.world) return

                    if (!InjectionUtil.get(LandBoard::class.java).verifyCanClaim(loc1) || !InjectionUtil.get(LandBoard::class.java).verifyCanClaim(loc2)) {
                        player.sendMessage(Chat.format("&cOne or more of the locations in your claim are unable to be claimed!"))
                        return
                    }

                    val claim = Cuboid(loc1, loc2)
                    team.claims.add(claim)
                    HCF.instance.landBoard.claims[claim] = team
                    team.save()

                    HCF.instance.landBoard.sessions.remove(player.uniqueId)
                    player.sendMessage(Chat.format("&aAdded a team claim for " + team.displayName))

                } else {
                    player.sendMessage(Chat.format("&cBoth claim positions must be set"))
                }

            }
        }
    }
}