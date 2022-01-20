package me.ninetyeightping.hcf.players.commands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.util.Chat
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Name
import me.vaperion.blade.annotation.Permission
import me.vaperion.blade.annotation.Sender
import org.bukkit.entity.Player


class EconomyCommands {

    @Command(value = ["adminpay"])
    @Permission(value = "hcf.admin", message = "No Permission.")
    fun pay(@Sender sender: Player, @Name("target")target: Player, @Name("amount")amount: Double) {
        val hcfplayer = HCF.instance.hcfPlayerHandler.byPlayer(target)
        if (hcfplayer == null) {
            sender.sendMessage(Chat.format("&cPlayer not found"))
            return
        }
        hcfplayer.balance = (hcfplayer.balance + amount)
        hcfplayer.push()
        sender.sendMessage(Chat.format("&aAdded $" + amount + " to " + target.name + "'s balance"))
    }
}