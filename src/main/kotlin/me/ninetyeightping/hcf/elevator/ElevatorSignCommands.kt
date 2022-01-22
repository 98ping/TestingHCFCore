package me.ninetyeightping.hcf.elevator

import me.ninetyeightping.hcf.util.Chat
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Permission
import me.vaperion.blade.annotation.Sender
import org.bukkit.Location
import org.bukkit.entity.Player

class ElevatorSignCommands {


    @Command(value = ["elevator debug"])
    @Permission("hcf.owner", message = "No Permission.")
    fun debug(@Sender player: Player) {

        var toTeleportLocationFound: Location? = null
        var locationFound: Boolean = false

        for (y in player.location.blockY..256) {

            if (!locationFound) {
                val cloneableLocation = player.location.clone()
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