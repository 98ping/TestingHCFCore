package me.ninetyeightping.hcf.team.comands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.team.types.FactionType
import me.ninetyeightping.hcf.util.Chat
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Name
import me.vaperion.blade.annotation.Sender
import org.bukkit.entity.Player


class GenericTeamCommands {

    @Command(value = ["team who", "f who", "t who"])
    fun teamInfo(@Sender sender: Player, @Name("target")player: Player) {

        val team = HCF.instance.teamHandler.byPlayer(player)
        if (team == null) {
            sender.sendMessage(Chat.format("&cTeam not found!"))
            return
        }

        val sendTo = sender
        sendTo.sendMessage(Chat.format("&9${team.displayName}"))
        sendTo.sendMessage(Chat.format("&eBalance: &f" + team.balance))
        sendTo.sendMessage(Chat.format("&eMembers: &f" + team.getNamedMembers().toString().replace("[", "").replace("]", "")))

    }

    @Command(value = ["team create", "f create", "t create"])
    fun createTeam(@Sender player: Player, @Name("name")name: String) {


        val team = Team(name.toLowerCase(), name, ArrayList(), player.uniqueId.toString(), 0.0, ArrayList(), false, ArrayList(), FactionType.PLAYER)

        //add creator to the list
        team.members.add(player.uniqueId.toString())


        HCF.instance.teamHandler.createTeam(team)
        player.sendMessage(Chat.format("&eCreated a team with the name of $name"))

    }

}
