package me.ninetyeightping.hcf.team.claims.player

import org.bukkit.Location
import java.util.*

data class ClaimSession(
    var uuid: UUID,
    var position1: Location?,
    var position2: Location?
)
