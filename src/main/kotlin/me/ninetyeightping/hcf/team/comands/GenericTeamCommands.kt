package me.ninetyeightping.hcf.team.comands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.util.Chat
import org.bukkit.entity.Player
import revxrsal.commands.annotation.*
import revxrsal.commands.bukkit.BukkitCommandActor


@Command("team")
class GenericTeamCommands {

    @Subcommand("info")
    @Usage("info <player>")
    fun teamInfo(actor: BukkitCommandActor, player: Player) {
        if (!actor.isPlayer) return
        if (player == null) {
            actor.reply(Chat.format("&cInvalid player"))
            return
        }

        val team = HCF.instance.teamHandler.byPlayer(player)
        if (team == null) {
            actor.reply(Chat.format("&cTeam not found!"))
            return
        }

        val sendTo = actor.asPlayer;
        sendTo?.sendMessage("&b== &e" + team.displayName + "&b==")
        sendTo?.sendMessage("&eBalance: &f" + team.balance)

    }

    @Subcommand("create")
    @Usage("create <name>")
    @Description("Creates a team with a provided name")
    fun createTeam(actor: BukkitCommandActor, name: String) {
        if (!actor.isPlayer) {
            actor.reply(Chat.format("&cYou are not a player so you are unable to use this command"))
            return
        }

        val player = actor.asPlayer
        if (player == null) {
            actor.reply("&cNull player. Returning")
            return
        }

        val team = Team(name.toLowerCase(), name, ArrayList(), player.uniqueId.toString(), 0.0, ArrayList(), false)

        //add creator to the list
        team.members.add(player.uniqueId.toString())


        HCF.instance.teamHandler.createTeam(team)
        actor.reply(Chat.format("&eCreated a team with the name of $name"))

    }

}
