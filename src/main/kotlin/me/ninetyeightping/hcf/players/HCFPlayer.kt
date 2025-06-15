package me.ninetyeightping.hcf.players

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import java.util.*

data class HCFPlayer(
    var uuid: UUID,
    var name: String,
    var balance: Double = 0.0,
    var stats: StatisticEntry = StatisticEntry(),
) {

    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }

    fun push() {
        HCF.instance.playerHandler.save(this)
    }
}
