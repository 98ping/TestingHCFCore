package me.ninetyeightping.hcf.players

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import java.util.*

data class HCFPlayer(
    var uuid: UUID,
    var name: String,
    var balance: Double = 0.0,
    var stats: StatisticEntry = StatisticEntry(0, 0, 0, 0),
) {


    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }

    fun push() {
        HCF.instance.playerHandler.save(this)
    }
}
