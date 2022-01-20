package me.ninetyeightping.hcf.team.claims

import me.ninetyeightping.hcf.HCF
import org.bukkit.Location
import org.bukkit.World

class LandBoard {

    var claims = arrayListOf<Claim>()

    init {

        for (team in HCF.instance.teamHandler.teams) {
            team.claims.forEach {
                claims.add(it)
            }
        }
    }

    fun claimByLocation(location: Location) : Claim? {
        return claims.stream().filter { it.owningCuboid.contains(location) }.findFirst().orElse(null)
    }

    fun isWarzone(loc: Location): Boolean {
        return if (loc.world.environment != World.Environment.NORMAL) {
            false
        } else Math.abs(loc.blockX) <= 1000 && Math.abs(loc.blockZ) <= 1000 || Math.abs(loc.blockX) > 1000 || Math.abs(loc.blockZ) > 1000
    }
 }