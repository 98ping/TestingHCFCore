package me.ninetyeightping.hcf.team.claims

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.team.claims.player.ClaimSession
import me.ninetyeightping.hcf.team.system.claims.SystemTeamClaimSession
import me.ninetyeightping.hcf.util.Cuboid
import me.ninetyeightping.hcf.util.InjectionUtil
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.springframework.stereotype.Service
import java.util.*

@Service
class LandBoard {

    //pure mortal claiming
    var claims = hashMapOf<Cuboid, Team>()
    var sessions = hashMapOf<UUID, ClaimSession>()

    //superior being claiming
    var systemSessions = hashMapOf<UUID, SystemTeamClaimSession>()

    val WARZONE_RADIUS = 1000
    val WARZONE_BORDER = 3000

    init {

        //thank you spring beans
        for (team in HCF.instance.teamHandler.teams) {
            team.claims.forEach {
                claims[it] = team
            }
        }
    }

    fun verifyCanClaim(location: Location) : Boolean {
        if (isWarzone(location)) return false

        if (claimByLocation(location) != null) return false

        return true
    }

    fun refreshTeams() {
        for (team in InjectionUtil.get(TeamHandler::class.java).teams) {
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
        val team = claims.getOrDefault(cuboid, null)
        if (team != null) {
            return team
        }

        return null
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