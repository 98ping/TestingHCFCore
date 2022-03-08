package me.ninetyeightping.hcf.team.comands

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.team.claims.LandBoard
import me.ninetyeightping.hcf.team.claims.listener.ClaimListener
import me.ninetyeightping.hcf.team.claims.player.ClaimSession
import me.ninetyeightping.hcf.team.types.FactionType
import me.ninetyeightping.hcf.timers.impl.CombatTimer
import me.ninetyeightping.hcf.timers.impl.FHomeTimer
import me.ninetyeightping.hcf.util.Chat
import me.vaperion.blade.annotation.*
import org.bukkit.Bukkit
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
        if (!HCF.instance.teamHandler.exists(name)) {
            player.sendMessage(Chat.format("&cTeam not found."))
            return
        }

        val team = HCF.instance.teamHandler.byName(name)
        team!!.dtrregen = (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1))
        team.save()
        player.sendMessage(Chat.format("&aPut $name on DTR regen"))

    }

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
        val team = HCF.instance.teamHandler.byPlayer(sender)
        if (team != null) {
            sender.sendMessage(Chat.format("&cYou are currently on a team"))
            return
        }

        val tryingToAcceptTeam = HCF.instance.teamHandler.byName(teamString)
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
        val team = HCF.instance.teamHandler.byPlayer(sender)
        if (team == null) {
            sender.sendMessage(Chat.format("&cYou are not on a team currently"))
            return
        }

        val hcfplayer = HCF.instance.playerHandler.byPlayerName(target)

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


    @Command(value = ["team who", "f who", "t who", "f i"])
    fun teamInfo(@Sender sender: Player, @Name("target") player: String) {

        val factionByName = HCF.instance.teamHandler.byName(player)

        if (factionByName != null) {
            factionByName.sendTeamInfo(sender)
        }

        val hcf = HCF.instance.playerHandler.byPlayerName(player)


        val factionByPlayer = HCF.instance.teamHandler.byUUID(UUID.fromString(hcf!!.uuid))


        if (factionByPlayer != null) {
            factionByPlayer.sendTeamInfo(sender)
        }

        if (factionByName == null && factionByPlayer == null) {
            sender.sendMessage(Chat.format("&cFaction not found"))
            return
        }






    }

    @Command(value = ["team kick", "f kick", "t kick"])
    fun kik(@Sender sender: Player, @Name("target")target: String) {
        val team = HCF.instance.teamHandler.byPlayer(sender)
        if (team == null) {
            sender.sendMessage(Chat.format("&cYou are not on a team currently"))
            return
        }

        val player = HCF.instance.playerHandler.byPlayerName(target)

        if (player == null) {
            sender.sendMessage(Chat.format("&cIssue finding player. Report this"))
            return
        }

        team.members.remove(player.uuid)
        team.save()
        team.sendGlobalTeamMessage(Chat.format("&e" + player.name + " was kicked from the team"))
        if (Bukkit.getPlayer(UUID.fromString(player.uuid)) != null) {
            Bukkit.getPlayer(UUID.fromString(player.uuid)).sendMessage(Chat.format("&cYou were kicked from the faction!"))
        }
    }


    @Command(value = ["team promote", "f promote", "t promote"])
    fun promo(@Sender sender: Player, @Name("target")target: String) {
        val team = HCF.instance.teamHandler.byPlayer(sender)
        if (team == null) {
            sender.sendMessage(Chat.format("&cYou are not on a team currently"))
            return
        }

        val player = HCF.instance.playerHandler.byPlayerName(target)

        if (player == null) {
            sender.sendMessage(Chat.format("&cIssue finding player. Report this"))
            return
        }

        if (team.subLeaders.contains(player.uuid)) {
            sender.sendMessage(Chat.format("&cPlayer is already a subleader"))
            return
        }

        if (team.leader!!.equals(player.uuid)) {
            sender.sendMessage(Chat.format("&cPlayer is already a leader of this faction!"))
        }

        team.subLeaders.add(player.uuid)
        team.save()
        sender.sendMessage(Chat.format("&aPromoted $target to a subleader"))
    }

    @Command(value = ["team kick", "f kick", "t kick"])
    fun kick(@Sender sender: Player, @Name("target")target: HCFPlayer) {
        val team = HCF.instance.teamHandler.byPlayer(sender)
        if (team == null) {
            sender.sendMessage(Chat.format("&cYou are not on a team currently"))
            return
        }

        if (!team.subLeaders.contains(sender.uniqueId.toString())) {
            sender.sendMessage(Chat.format("&cYou must be a subleader to kick someone"))
            return
        }

        if (team.leader.equals(target.uuid)) {
            sender.sendMessage(Chat.format("&cYou cannot kick a leader"))
            return
        }

        team.members.remove(target.uuid)
        if (team.subLeaders.contains(target.uuid)) {
            team.subLeaders.remove(target.uuid)
        }


        team.sendGlobalTeamMessage("&e" + target.name + " has been kicked from the faction")

        if (Bukkit.getPlayer(UUID.fromString(target.uuid)) != null) {

            Bukkit.getPlayer(UUID.fromString(target.uuid)).sendMessage(Chat.format("&cYou have been kicked from your faction"))
        }

    }

    @Command(value = ["team claim", "f claim", "t claim"])
    fun claim(@Sender sender: Player) {
        val team = HCF.instance.teamHandler.byPlayer(sender)
        if (team == null) {
            sender.sendMessage(Chat.format("&cYou are not on a team currently"))
            return
        }

        if (!team.subLeaders.contains(sender.uniqueId.toString()) && !team.leader.equals(sender.uniqueId.toString())) {
            sender.sendMessage(Chat.format("&cYou must be a subleader to start the claiming process"))
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

        if (HCF.instance.teamHandler.byPlayer(player) != null) {
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


        HCF.instance.teamHandler.createTeam(team)
        HCF.instance.teamHandler.addDTRAndMemberToTeam(player, team)
        player.sendMessage(Chat.format("&eCreated a team with the name of $name"))

    }

    @Command(value = ["team disband", "f disband", "t disband"])
    fun disbandTeam(@Sender player: Player) {

        val teamForPlayer = HCF.instance.teamHandler.byPlayer(player)

        if (teamForPlayer == null) {
            player.sendMessage(Chat.format("&cYou are not on a team"))
            return
        }

        if (!teamForPlayer.leader.equals(player.uniqueId.toString(), ignoreCase = true)) {
            player.sendMessage(Chat.format("&cYou are not the leader of this faction"))
            return
        }

        teamForPlayer.sendGlobalTeamMessage("&eThe team has been disbanded")
        teamForPlayer.claims.forEach { HCF.instance.landBoard.claims.remove(it) }
        HCF.instance.teamHandler.disbandTeam(teamForPlayer)
    }

}
