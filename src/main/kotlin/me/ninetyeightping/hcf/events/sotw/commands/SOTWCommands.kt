package me.ninetyeightping.hcf.events.sotw.commands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.TimeUtils
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Name
import me.vaperion.blade.annotation.Permission
import me.vaperion.blade.annotation.Sender
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SOTWCommands {

    @Command(value = ["sotw start"])
    @Permission("hcf.sotw", message = "No Permission.")
    fun sotwstart(@Sender sender: CommandSender, @Name("duration")time: String) {
        HCF.instance.sotwHandler.startSOTWTimer(TimeUtils.parseTime(time))
    }

    @Command(value = ["sotw enable"])
    fun sotwstart(@Sender player: Player) {
        if (!HCF.instance.sotwHandler.serverIsOnSOTWTimer()) {
            player.sendMessage(Chat.format("&cThere is no SOTW timer currently active"))
            return
        }

        if (HCF.instance.sotwHandler.isSOTWEnabled(player)) {
            player.sendMessage(Chat.format("&cYou are already SOTW enabled."))
            return
        }

        HCF.instance.sotwHandler.enableSotwTimer(player)
        player.sendMessage(Chat.format("&aEnabled your &a&lSOTW &atimer."))
    }
}