package me.ninetyeightping.hcf.team.claims.listener

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.claims.LandBoard
import me.ninetyeightping.hcf.team.system.flags.Flag
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.Cuboid
import me.ninetyeightping.hcf.util.ItemBuilder
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

class ClaimListener : Listener {

    companion object {
        val claimWand = ItemBuilder.of(Material.GOLD_HOE).name("&cClaiming Wand").setUnbreakable(true).build()
    }


    @EventHandler
    fun tryAndPlaceInClaim(event: BlockPlaceEvent) {
        val location = event.block.location

        if (HCF.instance.landBoard.claimByLocation(location) != null) {
            if (event.player.gameMode != GameMode.CREATIVE) {
                val claim = HCF.instance.landBoard.claimByLocation(location)
                val team = HCF.instance.landBoard.teamByClaim(claim!!)

                if (!team!!.isMember(event.player) && !team.isRaidable()) {
                    event.isCancelled = true
                    event.player.sendMessage(Chat.format("&eYou cannot place blocks inside of " + team.color + team.fakeName + "&e's claim"))
                }
            }
        }
    }

    @EventHandler
    fun generalDamageEvent(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            val damager = event.damager as Player
            val entity = event.entity as Player

            val claim = HCF.instance.landBoard.getTeamClaimFromPlayer(entity.location)
            val damagerClaim = HCF.instance.landBoard.getTeamClaimFromPlayer(damager.location)

            if (claim != null && damagerClaim != null) {

                if (claim.masks.contains(Flag.SAFEZONE) && !damagerClaim.masks.contains(Flag.SAFEZONE)) {
                    event.isCancelled = true
                    damager.sendMessage(Chat.format("&eYou cannot damage people in a &aSafe-Zone"))
                } else if (!claim.masks.contains(Flag.SAFEZONE) && damagerClaim.masks.contains(Flag.SAFEZONE)) {
                    event.isCancelled = true
                    damager.sendMessage(Chat.format("&eYou cannot damage people in a &aSafe-Zone"))
                }
            }

            val team = HCF.instance.teamHandler.byPlayer(entity)
            val damagerTeam = HCF.instance.teamHandler.byPlayer(damager)
            if (team == damagerTeam) {
                damager.sendMessage(Chat.format("&eYou cannot damage your teammates"))
                event.isCancelled = true
            }
        }

        @EventHandler
        fun tryAndTakeDamageInSpawnRegion(event: EntityDamageEvent) {
            val claimByLocation = HCF.instance.landBoard.claimByLocation(event.entity.location) ?: return

            val teamByClaimLocation = HCF.instance.landBoard.teamByClaim(claimByLocation) ?: return

            if (teamByClaimLocation.masks.contains(Flag.SAFEZONE)) {
                event.isCancelled = true
            }
        }

        @EventHandler
        fun tryAndBreakInClaim(event: BlockBreakEvent) {
            val location = event.block.location

            if (HCF.instance.landBoard.claimByLocation(location) != null) {
                if (event.player.gameMode != GameMode.CREATIVE) {
                    val claim = HCF.instance.landBoard.claimByLocation(location)
                    val team = HCF.instance.landBoard.teamByClaim(claim!!)

                    if (!team!!.isMember(event.player) && !team.isRaidable()) {
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
                val team = HCF.instance.teamHandler.byPlayer(player) ?: return

                if (event.action == Action.LEFT_CLICK_BLOCK) {

                    event.isCancelled = true
                    val claimSession = HCF.instance.landBoard.sessions.getOrDefault(player.uniqueId, null) ?: return

                    claimSession.position1 = event.clickedBlock.location
                    player.sendMessage(Chat.format("&aUpdated position 1 to &f(" + event.clickedBlock.location.blockX + ", " + event.clickedBlock.location.blockY + ", " + event.clickedBlock.location.blockZ + ")"))
                    return
                }

                if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    event.isCancelled = true

                    val claimSession = HCF.instance.landBoard.sessions.getOrDefault(player.uniqueId, null) ?: return

                    claimSession.position2 = event.clickedBlock.location
                    player.sendMessage(Chat.format("&aUpdated position 2 to &f(" + event.clickedBlock.location.blockX + ", " + event.clickedBlock.location.blockY + ", " + event.clickedBlock.location.blockZ + ")"))
                    return
                }

                if (event.action == Action.LEFT_CLICK_AIR && player.isSneaking) {

                    event.isCancelled = true
                    val claimSession = HCF.instance.landBoard.sessions.getOrDefault(player.uniqueId, null) ?: return

                    if (claimSession.position1 != null && claimSession.position2 != null) {
                        val loc1 = claimSession.position1!!.clone()
                        loc1.y = 256.0

                        val loc2 = claimSession.position2!!.clone()
                        loc2.y = 0.0

                        if (loc1.world != loc2.world) {
                            player.sendMessage(Chat.format("&cOne of your claim positions are not in the same word as the other!"))
                            return
                        }

                        if (!HCF.instance.landBoard.verifyCanClaim(loc1) || !HCF.instance.landBoard.verifyCanClaim(loc2)
                        ) {
                            player.sendMessage(Chat.format("&cOne or more of the locations in your claim are unable to be claimed!"))
                            return
                        }

                        val claim = Cuboid(loc1, loc2)

                        team.claims.add(claim)
                        HCF.instance.landBoard.claims[claim] = team
                        team.save()

                        player.itemInHand = null
                        player.updateInventory()
                        HCF.instance.landBoard.sessions.remove(player.uniqueId)
                        player.sendMessage(Chat.format("&aAdded a team claim for " + team.displayName))
                    } else {
                        player.sendMessage(Chat.format("&cBoth claim positions must be set"))
                    }
                }
            }
        }
    }
}