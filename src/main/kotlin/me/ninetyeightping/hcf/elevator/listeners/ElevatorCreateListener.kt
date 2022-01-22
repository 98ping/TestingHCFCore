package me.ninetyeightping.hcf.elevator.listeners

import me.ninetyeightping.hcf.util.Chat
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class ElevatorCreateListener : Listener {

    @EventHandler
    fun createElevator(event: BlockPlaceEvent) {

        val block = event.block
        if (block.type == Material.SIGN || block.type == Material.SIGN_POST || block.type == Material.WALL_SIGN) {
            val signInfo = block.state as Sign

            if (signInfo.getLine(0).equals("[Elevator]", ignoreCase = true) && signInfo.getLine(1).equals(
                    "Up",
                    ignoreCase = true
                )
            ) {


                signInfo.setLine(0, "&9[Elevator]")
                signInfo.setLine(1, "Up")
                signInfo.update()
            }

        }


    }
}