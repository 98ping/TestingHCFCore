package me.ninetyeightping.hcf.players.commands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.InjectionUtil
import me.vaperion.blade.annotation.*
import org.bukkit.entity.Player


class EconomyCommands {

    @Command(value = ["adminpay"])
    @Permission(value = "hcf.admin", message = "No Permission.")
    fun pay(@Sender sender: Player, @Name("target")target: Player, @Name("amount")amount: Double) {
        val hcfplayer = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayer(target)
        if (hcfplayer == null) {
            sender.sendMessage(Chat.format("&cPlayer not found"))
            return
        }
        hcfplayer.balance = (hcfplayer.balance + amount)
        hcfplayer.push()
        sender.sendMessage(Chat.format("&aAdded $" + amount + " to " + target.name + "'s balance"))
    }

    @Command(value = ["bal", "balance"])
    fun bal(@Sender sender: Player, @Optional("self") target: Player) {

        val hcfplayerSender = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayer(sender)
        val hcfplayerTarget = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayer(target)

        if (hcfplayerSender != null && hcfplayerTarget == null) {
            sender.sendMessage(Chat.format("&c" + hcfplayerSender.name + "'s Balance: &f$" + hcfplayerSender.balance))
        } else {
            sender.sendMessage(Chat.format("&c" + hcfplayerTarget!!.name + "'s Balance: &f$" + hcfplayerTarget!!.balance))
        }
    }
}