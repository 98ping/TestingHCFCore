package me.ninetyeightping.hcf.players.stat

import me.ninetyeightping.hcf.HCF

data class StatisticEntry(
    var kills: Int = 0,
    var killstreak: Int = 0,
    var deaths: Int = 0,
    var kothCaptures: Int = 0,
) {
    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }
}
