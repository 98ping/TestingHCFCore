package me.ninetyeightping.hcf.team

import lombok.AllArgsConstructor
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayer
import me.ninetyeightping.hcf.team.claims.Claim
import me.ninetyeightping.hcf.team.types.FactionType
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

@AllArgsConstructor
data class Team(
    val id: String,
    val displayName: String,
    val members: ArrayList<String>,
    val leader: String,
    val balance: Double,
    val pendingInvites: ArrayList<String>,
    val needsSave: Boolean,
    val claims: ArrayList<Claim>,
    val teamType: FactionType
) {

    fun globalDisplay(player: Player) : String {
        if (isMember(player)) {
            return "&2$displayName"
        }

        if (!isMember(player)) {
            return "&c$displayName"
        }
        return "&c$displayName"
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








