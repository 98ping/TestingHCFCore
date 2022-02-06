package me.ninetyeightping.hcf.team.comands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.team.claims.LandBoard
import me.ninetyeightping.hcf.team.claims.listener.ClaimListener
import me.ninetyeightping.hcf.team.claims.player.ClaimSession
import me.ninetyeightping.hcf.team.types.FactionType
import me.ninetyeightping.hcf.timers.impl.CombatTimer
import me.ninetyeightping.hcf.timers.impl.FHomeTimer
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.InjectionUtil
import me.vaperion.blade.annotation.*
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class GenericTeamCommands {

    @Command(value = ["team forcedtrregen"])
    @Permission(value = "hcf.admin", message = "No Permission.")
    fun forcesetdtrregen(@Sender player: Player, @Name("team")name: String) {
        if (!InjectionUtil.get(TeamHandler::class.java).exists(name)) {
            player.sendMessage(Chat.format("&cTeam not found."))
            return
        }

        val team = InjectionUtil.get(TeamHandler::class.java).byName(name)
        team!!.dtrregen = (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1))
        team.save()
        player.sendMessage(Chat.format("&aPut $name on DTR regen"))

    }

    @Command(value = ["f sethome", "team sethome", "t sethome"])
    fun sethome(@Sender player: Player) {
        val team = InjectionUtil.get(TeamHandler::class.java).byPlayer(player)
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
        val team = InjectionUtil.get(TeamHandler::class.java).byPlayer(player)
        if (team == null) {
            player.sendMessage(Chat.format("&cNo team found!"))
            return
        }

        if (team.teamLocation == null) {
            player.sendMessage(Chat.format("&cYou do not have a home to teleport to."))
            return
        }

        if (CombatTimer.hasCooldown(player)) {
            player.sendMessage(Chat.format("&cYou are currently under &4Combat &ctag"))
            return
        }

        FHomeTimer.addCooldown(player)
        object : BukkitRunnable() {
            var seconds = 0;
            override fun run() {

                if (!FHomeTimer.hasCooldown(player)) {
                    player.sendMessage(Chat.format("&cYour &bHome &ctimer was removed."))
                    cancel()
                }

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

    @Command(value = ["team accept", "f accept", "t accept"])
    fun accept(@Sender sender: Player, @Name("team")teamString: String) {
        val team = InjectionUtil.get(TeamHandler::class.java).byPlayer(sender)
        if (team != null) {
            sender.sendMessage(Chat.format("&cYou are currently on a team"))
            return
        }

        val tryingToAcceptTeam = InjectionUtil.get(TeamHandler::class.java).byName(teamString)
        if (tryingToAcceptTeam == null) {
            sender.sendMessage(Chat.format("&cThis team you are trying to accept is null"))
            return
        }

        if (!tryingToAcceptTeam.pendingInvites.contains(sender.uniqueId.toString())) {
            sender.sendMessage(Chat.format("&cThis team has not invited you"))
            return
        }

        tryingToAcceptTeam.members.add(sender.uniqueId.toString())
        tryingToAcceptTeam.pendingInvites.remove(sender.uniqueId.toString())
        tryingToAcceptTeam.setMaximumDTR()
        tryingToAcceptTeam.save()
        tryingToAcceptTeam.sendGlobalTeamMessage("&e" + sender.name + " &ehas joined the team!")
        sender.sendMessage(Chat.format("&aJoined the team!"))
    }

    @Command(value = ["team invite", "f invite", "t invite"])
    fun invite(@Sender sender: Player, @Name("target")target: String) {
        val team = InjectionUtil.get(TeamHandler::class.java).byPlayer(sender)
        if (team == null) {
            sender.sendMessage(Chat.format("&cYou are not on a team currently"))
            return
        }

        val hcfplayer = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayerName(target)

        if (hcfplayer == null) {
            sender.sendMessage(Chat.format("&cPlayer has never logged in under this alias"))
            return
        }

        if (!team.subLeaders.contains(sender.uniqueId.toString()) || !team.leader.equals(sender.uniqueId.toString())) {
            sender.sendMessage(Chat.format("&cYou must be a subleader to invite a user"))
            return
        }

        team.pendingInvites.add(hcfplayer.uuid)
        team.save()
        team.sendGlobalTeamMessage("&e" + hcfplayer.name + " &ehas been invited to the team")

    }


    @Command(value = ["team who", "f who", "t who"])
    fun teamInfo(@Sender sender: Player, @Name("target") player: String) {

        val hcfplayer = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayerName(player)
        if (hcfplayer == null) {
            sender.sendMessage(Chat.format("&cNo target with the name $player found"))
            return
        }

        val teambyPlayer = InjectionUtil.get(TeamHandler::class.java).byUUID(UUID.fromString(hcfplayer.uuid))

        if (teambyPlayer == null) {
            sender.sendMessage(Chat.format("&cNo team has this player in their roster"))
            return
        }

        teambyPlayer.sendTeamInfo(sender)




    }

    @Command(value = ["team", "f", "t"])
    fun info(@Sender sender: Player) {
        sender.sendMessage(Chat.format("&eFaction Information"))
        sender.sendMessage("&7&m-------------------------------------")
        sender.sendMessage(Chat.format("&6Normal Commands"))
        sender.sendMessage(Chat.format("&e/team create"))
        sender.sendMessage(Chat.format("&e/team disband"))
        sender.sendMessage(Chat.format("&e/team who"))
        sender.sendMessage(Chat.format("&e/team accept"))
        sender.sendMessage(Chat.format("&e/team home"))
        sender.sendMessage(" ")
        sender.sendMessage(Chat.format("&6Subleader Commands"))
        sender.sendMessage(Chat.format("&e/team claim"))
        sender.sendMessage(Chat.format("&e/team invite"))
        sender.sendMessage(Chat.format("&e/team kick"))
        sender.sendMessage(Chat.format("&e/team sethome"))
        sender.sendMessage("&7&m-------------------------------------")

    }

    @Command(value = ["team claim", "f claim", "t claim"])
    fun claim(@Sender sender: Player) {
        val team = InjectionUtil.get(TeamHandler::class.java).byPlayer(sender)
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

        if (InjectionUtil.get(TeamHandler::class.java).exists(name)) {
            player.sendMessage(Chat.format("&cTeam already exists."))
            return
        }

        if (InjectionUtil.get(TeamHandler::class.java).byPlayer(player) != null) {
            player.sendMessage(Chat.format("&cYou are already on a team!"))
            return
        }

        val team = Team(
            name.toLowerCase(),
            name,
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
            0.0,
            0L,
            ArrayList()
        )

        //add creator to the list
        team.members.add(player.uniqueId.toString())

        team.dtr = team.calculateMaximumDTR()

        InjectionUtil.get(TeamHandler::class.java).createTeam(team)
        player.sendMessage(Chat.format("&eCreated a team with the name of $name"))

    }

    @Command(value = ["team disband", "f disband", "t disband"])
    fun disbandTeam(@Sender player: Player) {

        val teamForPlayer = InjectionUtil.get(TeamHandler::class.java).byPlayer(player)

        if (teamForPlayer == null) {
            player.sendMessage(Chat.format("&cYou are not on a team"))
            return
        }

        if (!teamForPlayer.leader.equals(player.uniqueId.toString(), ignoreCase = true)) {
            player.sendMessage(Chat.format("&cYou are not the leader of this faction"))
            return
        }

        teamForPlayer.sendGlobalTeamMessage("&eThe team has been disbanded")
        teamForPlayer.claims.forEach { InjectionUtil.get(LandBoard::class.java).claims.remove(it) }
        InjectionUtil.get(TeamHandler::class.java).disbandTeam(teamForPlayer)
    }

}
