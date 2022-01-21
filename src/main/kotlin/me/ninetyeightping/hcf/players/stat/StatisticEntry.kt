package me.ninetyeightping.hcf.players.stat

import me.ninetyeightping.hcf.HCF

data class StatisticEntry(
    var kills: Int,
    var killstreak: Int,
    var deaths: Int,
    var kothCaptures: Int
) {



    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }
}
