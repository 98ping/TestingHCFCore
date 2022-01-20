package me.ninetyeightping.hcf.events.sotw.commands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.util.TimeUtils
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Name
import me.vaperion.blade.annotation.Permission
import me.vaperion.blade.annotation.Sender
import org.bukkit.command.CommandSender

class SOTWCommands {

    @Command(value = ["sotw start"])
    @Permission("hcf.sotw", message = "No Permission.")
    fun sotwstart(@Sender sender: CommandSender, @Name("duration")time: String) {
        HCF.instance.sotwHandler.startSOTWTimer(TimeUtils.parseTime(time))
    }
}