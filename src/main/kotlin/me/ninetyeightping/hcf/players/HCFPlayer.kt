package me.ninetyeightping.hcf.players

import me.ninetyeightping.hcf.HCF
import org.bukkit.scoreboard.Team

data class HCFPlayer(
    val uuid: String,
    val name: String,
    val balance: Double,
    val team: String,
) {


    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }
}
