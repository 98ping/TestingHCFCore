package me.ninetyeightping.hcf.team.claims.listener

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.util.Chat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class LandBoardListener : Listener {

    @EventHandler
    fun move(event: PlayerMoveEvent) {
        val player = event.player;

        if (HCF.instance.landBoard.claimByLocation(event.to) != null && HCF.instance.landBoard.claimByLocation(event.from) == null) {
            println("a")
            val toClaim = HCF.instance.landBoard.claimByLocation(event.to)

            player.sendMessage(Chat.format("&eNow Entering: " + HCF.instance.landBoard.teamByClaim(toClaim!!)!!.globalDisplay(player)))
            if (HCF.instance.landBoard.isWarzone(event.from)) {
                player.sendMessage(Chat.format("&eNow Leaving: &cWarzone"))
            } else {
                player.sendMessage(Chat.format("&eNow Leaving: &2Wilderness"))
            }


        }


        if (HCF.instance.landBoard.claimByLocation(event.to) != null && HCF.instance.landBoard.claimByLocation(event.from) != null) {
            println("b")
            val toClaim = HCF.instance.landBoard.claimByLocation(event.to)
            val fromClaim = HCF.instance.landBoard.claimByLocation(event.from)

            if (toClaim != fromClaim) {
                player.sendMessage(Chat.format("&eNow Entering: " + HCF.instance.landBoard.teamByClaim(toClaim!!)!!.globalDisplay(player)))
                player.sendMessage(Chat.format("&eNow Leaving: " + HCF.instance.landBoard.teamByClaim(fromClaim!!)!!.globalDisplay(player)))
            }
        }

        if (HCF.instance.landBoard.claimByLocation(event.to) == null && HCF.instance.landBoard.claimByLocation(event.from) != null) {
            println("c")
            val fromClaim = HCF.instance.landBoard.claimByLocation(event.from)

            if (HCF.instance.landBoard.isWarzone(event.to)) {
                player.sendMessage(Chat.format("&eNow Entering: &cWarzone"))
            } else {
                player.sendMessage(Chat.format("&eNow Entering: &2Wilderness"))
            }
            player.sendMessage(Chat.format("&eNow Leaving: " + HCF.instance.landBoard.teamByClaim(fromClaim!!)!!.globalDisplay(player)))
        }
    }

}