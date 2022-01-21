package me.ninetyeightping.hcf.team.system.claims

import me.ninetyeightping.hcf.team.Team
import org.bukkit.Location
import java.util.*

data class SystemTeamClaimSession(
    var uuid: UUID,
    var team: Team,
    var position1: Location?,
    var position2: Location?
)