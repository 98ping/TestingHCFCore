package me.ninetyeightping.hcf.team.comands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.team.claims.listener.ClaimListener
import me.ninetyeightping.hcf.team.claims.player.ClaimSession
import me.ninetyeightping.hcf.team.types.FactionType
import me.ninetyeightping.hcf.util.Chat
import me.vaperion.blade.annotation.Command
import me.vaperion.blade.annotation.Name
import me.vaperion.blade.annotation.Sender
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList


class GenericTeamCommands {

    @Command(value = ["team who", "f who", "t who"])
    fun teamInfo(@Sender sender: Player, @Name("target")player: Player) {

        val team = HCF.instance.teamHandler.byPlayer(player)
        if (team == null) {
            sender.sendMessage(Chat.format("&cTeam not found!"))
            return
        }

        val sendTo = sender
        sendTo.sendMessage(Chat.format("&7&m-------------------"))
        sendTo.sendMessage(Chat.format("&9${team.displayName}"))
        sendTo.sendMessage(Chat.format("&eBalance: &f" + team.balance))
        sendTo.sendMessage(Chat.format("&eMembers: &f" + team.getNamedMembers().toString().replace("[", "").replace("]", "")))
        sendTo.sendMessage(Chat.format("&7&m-------------------"))

    }

    @Command(value = ["team claim", "f claim", "t claim"])
    fun claim(@Sender sender: Player) {
        val team = HCF.instance.teamHandler.byPlayer(sender)
        if (team == null) {
            sender.sendMessage(Chat.format("&cYou are not on a team currently"))
            return
        }

        val claimSession = ClaimSession(sender.uniqueId, null, null)
        HCF.instance.landBoard.sessions[sender.uniqueId] = claimSession
        sender.inventory.addItem(ClaimListener.claimWand)
        sender.updateInventory()
        sender.sendMessage(Chat.format("&aStarted Claiming Process"))

    }

    @Command(value = ["team create", "f create", "t create"])
    fun createTeam(@Sender player: Player, @Name("name")name: String) {

        if (HCF.instance.teamHandler.exists(name)) {
            player.sendMessage(Chat.format("&cTeam already exists."))
            return
        }

        val team = Team(name.toLowerCase(), name, ArrayList(), ArrayList(), player.uniqueId.toString(), 0.0, ArrayList(), false, ArrayList(), FactionType.PLAYER)

        //add creator to the list
        team.members.add(player.uniqueId.toString())


        HCF.instance.teamHandler.createTeam(team)
        player.sendMessage(Chat.format("&eCreated a team with the name of $name"))

    }

}
