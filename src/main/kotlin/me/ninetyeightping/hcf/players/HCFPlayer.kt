package me.ninetyeightping.hcf.players

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import me.ninetyeightping.hcf.util.InjectionUtil
import org.bukkit.scoreboard.Team

data class HCFPlayer(
    var uuid: String,
    var name: String,
    var balance: Double,
    var stats: StatisticEntry
) {


    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }

    fun push() {
        InjectionUtil.get(HCFPlayerHandler::class.java).save(this)
    }
}
