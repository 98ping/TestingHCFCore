package me.ninetyeightping.hcf.team

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.team.system.flags.Flag
import me.ninetyeightping.hcf.team.types.FactionType
import me.ninetyeightping.hcf.util.Cuboid
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

data class Team(
    var id: String,
    var displayName: String,
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
    var masks: ArrayList<Flag>
) {

    fun globalDisplay(player: Player) : String {
        if (color != "") {
            return color + displayName
        }

        if (isMember(player)) {
            return "&2$displayName"
        }

        if (!isMember(player)) {
            return "&c$displayName"
        }

        return "&c$displayName"
    }

    fun verifyTeamClaimLocation(player: Player) : Boolean {
        return claims.stream().filter { it.contains(player.location) }.findFirst().orElse(null) != null
    }

    fun getNamedMembers() : MutableList<String> {
        val playerlist = members.stream().map { HCF.instance.hcfPlayerHandler.byUUID(UUID.fromString(it)) }.collect(Collectors.toList()) as ArrayList<HCFPlayer?>
        return playerlist.stream().filter(Objects::nonNull).map { it!!.name }.collect(Collectors.toList())
    }

    fun isMember(player: Player) : Boolean {
        return members.contains(player.uniqueId.toString())
    }

    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }

    fun save() {
        HCF.instance.teamHandler.save(this)
    }

}








