package me.ninetyeightping.hcf.elevator.listeners

import me.ninetyeightping.hcf.util.Chat
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent


class ElevatorCreateListener : Listener {


    @EventHandler
    fun onSignCreate(event: SignChangeEvent) {
        if (event.getLine(0).equals("[Elevator]", ignoreCase = true)) {
            event.setLine(0, Chat.format("&c[Elevator]"))
            event.setLine(1, "Up")
            event.setLine(2, "")
            event.setLine(3, "")
        }
    }

    @EventHandler
    fun onSignUse(event: PlayerInteractEvent) {
        val player = event.player
        if (event.action == Action.RIGHT_CLICK_BLOCK && event.clickedBlock != null && (event.clickedBlock.type == Material.SIGN || event.clickedBlock.type == Material.SIGN_POST || event.clickedBlock.type == Material.WALL_SIGN)) {
            val sign: Sign = event.clickedBlock.state as Sign
            if (!sign.getLine(0).equals(Chat.format("&c[Elevator]")) || !sign.getLine(1)
                    .equals("Up")
            ) {
                return
            }

            var toTeleportLocationFound: Location? = null
            var locationFound: Boolean = false



            for (y in event.clickedBlock.location.blockY..256) {

                if (!locationFound) {
                    val cloneableLocation = event.clickedBlock.location.clone()
                    cloneableLocation.y = y.toDouble()  + 2.0


                    if (cloneableLocation.block.isEmpty) {

                        toTeleportLocationFound = cloneableLocation
                        locationFound = true

                    }
                }

            }

            if (toTeleportLocationFound == null) {
                player.sendMessage(Chat.format("&cNo teleport location found"))
                return
            }
            player.teleport(toTeleportLocationFound)


        }

    }

}