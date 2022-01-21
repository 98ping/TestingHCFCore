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
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.ArrayList


class GenericTeamCommands {

    @Command(value = ["f sethome", "team sethome", "t sethome"])
    fun sethome(@Sender player: Player) {
        val team = HCF.instance.teamHandler.byPlayer(player)
        if (team == null) {
            player.sendMessage(Chat.format("&cNo team found!"))
            return
        }

        if (player.world.environment != World.Environment.NORMAL) {
            player.sendMessage(Chat.format("&cLocation must be in the overworld"))
            return
        }

        if (!team.subLeaders.contains(player.uniqueId.toString()) && !team.leader.equals(player.uniqueId.toString(), ignoreCase = true)) {
            player.sendMessage(Chat.format("&cYou are not a subleader so you cannot set the F-Home!"))
            return
        }

        if (!team.verifyTeamClaimLocation(player)) {
            player.sendMessage(Chat.format("&cYou must be in your claim to set your F-Home!"))
            return
        }

        team.teamLocation = player.location
        team.save()
        player.sendMessage(Chat.format("&aUpdated the &9F-Home &alocation"))
    }

    @Command(value = ["f home", "team home", "t home"])
    fun home(@Sender player: Player) {
        val team = HCF.instance.teamHandler.byPlayer(player)
        if (team == null) {
            player.sendMessage(Chat.format("&cNo team found!"))
            return
        }

        if (HCF.instance.timerHandler.combatTimer.hasCooldown(player)) {
            player.sendMessage(Chat.format("&cYou are currently under &4Combat &ctag"))
            return
        }
        object : BukkitRunnable() {
            var seconds = 0;
            override fun run() {

                when (seconds) {
                    0 -> {
                        player.sendMessage(Chat.format("&eTeleporting to your HQ in &f10 Seconds"))
                    }

                    5 -> {
                        player.sendMessage(Chat.format("&eTeleporting to your HQ in &f5 Seconds"))
                    }

                    6 -> {
                        player.sendMessage(Chat.format("&eTeleporting to your HQ in &f4 Seconds"))
                    }

                    7 -> {
                        player.sendMessage(Chat.format("&eTeleporting to your HQ in &f3 Seconds"))
                    }

                    8 -> {
                        player.sendMessage(Chat.format("&eTeleporting to your HQ in &f2 Seconds"))
                    }

                    9 -> {
                        player.sendMessage(Chat.format("&eTeleporting to your HQ in &f1 Seconds"))
                    }

                    10 -> {
                        player.teleport(team.teamLocation)
                        player.sendMessage(Chat.format("&eTeleported to your HQ"))
                        cancel()
                    }

                }

                seconds++
            }

        }.runTaskTimer(HCF.instance, 0L, 20L)
    }


    @Command(value = ["team who", "f who", "t who"])
    fun teamInfo(@Sender sender: Player, @Name("target") player: Player) {

        val team = HCF.instance.teamHandler.byPlayer(player)
        if (team == null) {
            sender.sendMessage(Chat.format("&cTeam not found!"))
            return
        }

        val sendTo = sender
        sendTo.sendMessage(Chat.format("&7&m-------------------"))
        sendTo.sendMessage(Chat.format("&9${team.displayName}"))
        sendTo.sendMessage(Chat.format("&eBalance: &f" + team.balance))
        sendTo.sendMessage(
            Chat.format(
                "&eMembers: &f" + team.getNamedMembers().toString().replace("[", "").replace("]", "")
            )
        )
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
    fun createTeam(@Sender player: Player, @Name("name") name: String) {

        if (HCF.instance.teamHandler.exists(name)) {
            player.sendMessage(Chat.format("&cTeam already exists."))
            return
        }

        val team = Team(
            name.toLowerCase(),
            name,
            ArrayList(),
            ArrayList(),
            player.uniqueId.toString(),
            0.0,
            ArrayList(),
            false,
            ArrayList(),
            null,
            FactionType.PLAYER,
            "",
            ArrayList()
        )

        //add creator to the list
        team.members.add(player.uniqueId.toString())


        HCF.instance.teamHandler.createTeam(team)
        player.sendMessage(Chat.format("&eCreated a team with the name of $name"))

    }

}
