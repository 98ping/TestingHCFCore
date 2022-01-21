package me.ninetyeightping.hcf.events.koth.commands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.events.koth.types.Koth
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.Cuboid
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Name
import me.vaperion.blade.annotation.Permission
import me.vaperion.blade.annotation.Sender
import org.bukkit.entity.Player

class KothGenericCommands {


    @Command(value = ["koth setpos"])
    @Permission(value = "hcf.koth.admin", message = "No Permission.")
    fun deactivate(@Sender player: Player, @Name("koth")name: String, @Name("position")pos: String) {
        if (!HCF.instance.kothHandler.exists(name)) {
            player.sendMessage(Chat.format("&cKoth doesnt exist"))
            return
        }

        val koth = HCF.instance.kothHandler.byName(name)

        when (pos) {

            "1" -> {
                koth!!.loc1 = player.location
                koth.save()
                player.sendMessage(Chat.format("&aUpdated location 1"))
            }

            "2" -> {
                koth!!.loc2 = player.location
                koth.save()
                player.sendMessage(Chat.format("&aUpdated location 2"))
            }
        }
    }

    @Command(value = ["koth create"])
    @Permission(value = "hcf.koth.admin", message = "No Permission.")
    fun create(@Sender player: Player, @Name("name")name: String) {
        if (HCF.instance.kothHandler.exists(name)) {
            player.sendMessage(Chat.format("&cKoth already exists"))
            return
        }

        val newkoth = Koth(name, false, 300, null, null)
        HCF.instance.kothHandler.createKoth(newkoth)
        player.sendMessage(Chat.format("&aCreated a new koth"))
    }

    @Command(value = ["koth deactivate"])
    @Permission(value = "hcf.koth.admin", message = "No Permission.")
    fun deactivate(@Sender player: Player, @Name("koth")name: String) {
        if (!HCF.instance.kothHandler.exists(name)) {
            player.sendMessage(Chat.format("&cKoth doesnt exist"))
            return
        }

        val koth = HCF.instance.kothHandler.byName(name)
        koth!!.deactivate()
        player.sendMessage(Chat.format("&cDeactivated a koth"))
    }

    @Command(value = ["koth activate"])
    @Permission(value = "hcf.koth.admin", message = "No Permission.")
    fun activate(@Sender player: Player, @Name("koth")name: String) {
        if (!HCF.instance.kothHandler.exists(name)) {
            player.sendMessage(Chat.format("&cKoth doesnt exist"))
            return
        }

        val koth = HCF.instance.kothHandler.byName(name)
        koth!!.activate()
        player.sendMessage(Chat.format("&aActivated a koth"))
    }
}