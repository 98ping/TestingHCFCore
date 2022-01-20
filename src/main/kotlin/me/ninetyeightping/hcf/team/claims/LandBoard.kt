package me.ninetyeightping.hcf.team.claims

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.team.claims.player.ClaimSession
import me.ninetyeightping.hcf.util.Cuboid
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

class LandBoard {

    var claims = hashMapOf<Cuboid, Team>()
    var sessions = hashMapOf<UUID, ClaimSession>()

    val WARZONE_RADIUS = 1000
    val WARZONE_BORDER = 3000

    init {

        for (team in HCF.instance.teamHandler.teams) {
            team.claims.forEach {
                claims[it] = team
            }
        }
    }

    fun getClaimForScoreboard(player: Player): String {
        if (claimByLocation(player.location) != null) {
            val claim = claimByLocation(player.location)
            return teamByClaim(claim!!)!!.globalDisplay(player)
        } else if (claimByLocation(player.location) == null && isWarzone(player.location)) {
            return "&cWarzone"
        } else {
            return "&aWilderness"
        }
    }

    fun teamByClaim(cuboid: Cuboid): Team? {
        return claims.getOrDefault(cuboid, null)
    }

    fun claimByLocation(location: Location): Cuboid? {
        return claims.keys.stream().filter { it.contains(location) }.findFirst().orElse(null)
    }

    fun isWarzone(loc: Location): Boolean {
        return if (loc.world.environment != World.Environment.NORMAL) {
            false
        } else Math.abs(loc.blockX) <= WARZONE_RADIUS && Math.abs(loc.blockZ) <= WARZONE_RADIUS || Math.abs(loc.blockX) > WARZONE_BORDER || Math.abs(
            loc.blockZ
        ) > WARZONE_BORDER
    }
}