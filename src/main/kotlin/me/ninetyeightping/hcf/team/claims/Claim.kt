package me.ninetyeightping.hcf.team.claims

import me.ninetyeightping.hcf.team.Team
import me.ninetyeightping.hcf.util.Cuboid
import java.util.*

data class Claim(
    val id: UUID,
    val owningTeam: Team,
    val owningCuboid: Cuboid
)
