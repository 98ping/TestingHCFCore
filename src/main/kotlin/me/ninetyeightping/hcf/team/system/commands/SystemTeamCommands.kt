package me.ninetyeightping.hcf.team.system.commands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.team.system.claims.SystemTeamClaimSession
import me.ninetyeightping.hcf.team.system.claims.listeners.SystemTeamClaimListener
import me.ninetyeightping.hcf.team.types.FactionType
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.InjectionUtil
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Name
import me.vaperion.blade.annotation.Permission
import me.vaperion.blade.annotation.Sender
import org.bukkit.entity.Player

class SystemTeamCommands {

    @Command(value = ["systemteam create"])
    @Permission(value = "hcf.systemteams.admin", message = "No Permission.")
    fun systemteamcreate(@Sender player: Player, @Name("name")name: String) {
        if (InjectionUtil.get(TeamHandler::class.java).exists(name)) {
            player.sendMessage(Chat.format("&cSystem team already exists."))
            return
        }

        val systeam = Team(
            name.toLowerCase(),
            name,
            name,
            ArrayList(),
            ArrayList(),
            null,
            0.0,
            ArrayList(),
            false,
            ArrayList(),
            null,
            FactionType.SYSTEM,
            "",
            0.0,
            0L,
            ArrayList()
        )

        InjectionUtil.get(TeamHandler::class.java).createTeam(systeam)
        player.sendMessage(Chat.format("&aCreated a system team with the name &f$name"))
    }

    @Command(value = ["systemteam kothify"])
    @Permission(value = "hcf.systemteams.admin", message = "No Permission.")
    fun systemteamkothify(@Sender player: Player, @Name("team")name: String) {
        if (!InjectionUtil.get(TeamHandler::class.java).exists(name)) {
            player.sendMessage(Chat.format("&cSystem team not found."))
            return
        }

        val team = InjectionUtil.get(TeamHandler::class.java).byName(name)
        team!!.color = "&c&l"
        team.fakeName = (team.displayName + " Koth")
        team.save()
        player.sendMessage(Chat.format("&aUpdated color and name of $name to make it look like a koth"))

        HCF.instance.landBoard.refreshTeams()
    }

    @Command(value = ["systemteam setcolor"])
    @Permission(value = "hcf.systemteams.admin", message = "No Permission.")
    fun systemteamcolor(@Sender player: Player, @Name("team")name: String, @Name("color")color: String) {
        if (!InjectionUtil.get(TeamHandler::class.java).exists(name)) {
            player.sendMessage(Chat.format("&cSystem team not found."))
            return
        }

        val team = InjectionUtil.get(TeamHandler::class.java).byName(name)
        team!!.color = color
        team.save()
        player.sendMessage(Chat.format("&aUpdated color of $name"))

        HCF.instance.landBoard.refreshTeams()
    }

    @Command(value = ["systemteam claimfor"])
    @Permission(value = "hcf.systemteams.admin", message = "No Permission.")
    fun systemteamclaim(@Sender player: Player, @Name("team")name: String) {
        if (!InjectionUtil.get(TeamHandler::class.java).exists(name)) {
            player.sendMessage(Chat.format("&cSystem team not found."))
            return
        }

        val team = InjectionUtil.get(TeamHandler::class.java).byName(name)
        HCF.instance.landBoard.systemSessions[player.uniqueId] = SystemTeamClaimSession(player.uniqueId, team!!, null, null)
        player.inventory.addItem(SystemTeamClaimListener.claimWand)
        player.updateInventory()
        player.sendMessage(Chat.format("&aStarted Claiming Process"))


    }


}