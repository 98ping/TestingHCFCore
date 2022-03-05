package me.ninetyeightping.hcf.team

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.team.system.flags.Flag
import me.ninetyeightping.hcf.team.types.FactionType
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.Cuboid
import me.ninetyeightping.hcf.util.TimeUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import kotlin.collections.ArrayList

data class Team(
    var id: String,
    var displayName: String,
    var fakeName: String,
    var members: ArrayList<String>,
    var subLeaders: ArrayList<String>,
    var leader: String?,
    var balance: Double,
    var pendingInvites: ArrayList<String>,
    var needsSave: Boolean,
    var claims: ArrayList<Cuboid>,
    var teamLocation: Location?,
    var teamType: FactionType,
    var color: String,
    var dtr: Double,
    var dtrregen: Long,
    var masks: ArrayList<Flag>
) {



    fun isRaidable() : Boolean {
        return dtr <= 0
    }


    fun setMaximumDTR() {
        if (dtrregen != 0L) {
            dtr = calculateMaximumDTR()
            save()
        }
    }

    fun calculateMaximumDTR(): Double {
        return (members.size * 1.1)
    }

    fun globalDisplay(player: Player): String {
        if (color != "") {
            return color + fakeName
        }

        if (isMember(player)) {
            return "&2$fakeName"
        }

        if (!isMember(player)) {
            return "&c$fakeName"
        }

        return "&c$fakeName"
    }

    fun registerPlayerDeath(player: Player) {
        dtr = (dtr - 1.1)
        dtrregen = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
        sendGlobalTeamMessage("&c" + player.name + " &chas died and lost you &f1.1 DTR")
        sendGlobalTeamMessage("&cNew DTR: &f$dtr")
        save()
    }

    fun sendGlobalTeamMessage(message: String) {
        val players = members.map { Bukkit.getPlayer(UUID.fromString(it)) }.filter { Objects.nonNull(it) }
            .toCollection(ArrayList())

        players.forEach { it.sendMessage(Chat.format(message)) }
    }

    fun sendTeamInfo(sendTo: Player) {
        //copied this over to team
        val team = this
        sendTo.sendMessage(Chat.format("&7&m-------------------"))
        sendTo.sendMessage(Chat.format("&6${team.displayName} &7[" + team.members.stream().filter { Objects.nonNull(Bukkit.getPlayer(UUID.fromString(it))) }.count() + "/" + team.members.size + "&7]"))
        sendTo.sendMessage(
            Chat.format(
                "&eLeader: &f" + HCF.instance.playerHandler
                    .byUUID(UUID.fromString(team.leader))!!.name
            )
        )
        sendTo.sendMessage(
            Chat.format(
                "&eMembers: &f" + team.getNamedMembersExcludingLeader().toString().replace("[", "").replace("]", "")
            )
        )
        sendTo.sendMessage(Chat.format("&eClaims: &f" + team.claims.size))
        sendTo.sendMessage(Chat.format("&eBalance: &f" + team.balance))
        sendTo.sendMessage(Chat.format("&eDTR: &f" + team.dtr))
        if (dtrregen != 0L) {
            sendTo.sendMessage(Chat.format("&eDTR Regen: &f" + getDTRRegenScore(team.dtrregen)))
        }
        sendTo.sendMessage(Chat.format("&7&m-------------------"))
    }

    fun getDTRRegenScore(time: Long): String? {
        val diff = time - System.currentTimeMillis()
        return if (diff > 0) {
            TimeUtils.formatIntoAbbreviatedString((diff / 1000L).toInt())
        } else {
            null
        }
    }

    fun verifyTeamClaimLocation(player: Player): Boolean {
        return claims.stream().filter { it.contains(player.location) }.findFirst().orElse(null) != null
    }

    fun getNamedMembersExcludingLeader(): MutableList<String> {
        val map = members

        map.remove(leader)
        val playerlist =
            map.stream().map { HCF.instance.playerHandler.byUUID(UUID.fromString(it)) }
                .collect(Collectors.toList()) as ArrayList<HCFPlayer?>

        return playerlist.stream().filter(Objects::nonNull).map { it!!.name }.collect(Collectors.toList())
    }

    fun getNamedMembers(): MutableList<String> {
        val playerlist =
            members.stream().map { HCF.instance.playerHandler.byUUID(UUID.fromString(it)) }
                .collect(Collectors.toList()) as ArrayList<HCFPlayer?>
        return playerlist.stream().filter(Objects::nonNull).map { it!!.name }.collect(Collectors.toList())
    }

    fun isMember(player: Player): Boolean {
        return members.contains(player.uniqueId.toString())
    }

    fun construct(): String {
        return HCF.instance.gson.toJson(this)
    }


    fun save() {
        HCF.instance.teamHandler.save(this)
    }

}








