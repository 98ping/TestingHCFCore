package me.ninetyeightping.hcf.team

import lombok.AllArgsConstructor
import me.ninetyeightping.hcf.HCF

@AllArgsConstructor
data class Team(
    val id: String,
    val displayName: String,
    val members: ArrayList<String>,
    val leader: String,
    val balance: Double,
    val pendingInvites: ArrayList<String>,
    val needsSave: Boolean
) {

    fun construct() : String {
        return HCF.instance.gson.toJson(this)
    }

    fun save() {
        HCF.instance.teamHandler.save(this)
    }
}








