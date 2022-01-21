package me.ninetyeightping.hcf.board

import io.github.thatkawaiisam.assemble.AssembleAdapter
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.TimeUtils
import org.bukkit.entity.Player

class AssembleBoard : AssembleAdapter {

    override fun getTitle(player: Player?): String {
        return Chat.format("&c&lTesting")
    }

    override fun getLines(player: Player?): MutableList<String> {
        val lines = arrayListOf<String>()
        lines.add("&7&m--------------------")
        lines.add("&6&lClaim: &r" + HCF.instance.landBoard.getClaimForScoreboard(player!!))

        if (HCF.instance.timerHandler.enderpearlTimer.hasCooldown(player)) {
            lines.add("&9Enderpearl: &f" + getPearlScore(player))
        }

        if (HCF.instance.timerHandler.combatTimer.hasCooldown(player)) {
            lines.add("&4Combat: &f" + getCombatScore(player))
        }


        if (HCF.instance.sotwHandler.serverIsOnSOTWTimer()) {
            lines.add("&a&lSOTW: " + getTimerScore(HCF.instance.sotwHandler.globalDuration))
        }
        lines.add("&c&7&m--------------------")
        return lines;
    }

    fun getCombatScore(player: Player?) : String? {
        val diff = HCF.instance.timerHandler.combatTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoAbbreviatedString((diff / 1000L).toInt())
        }
        return null
    }

    fun getPearlScore(player: Player?) : String? {
        val diff = HCF.instance.timerHandler.enderpearlTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoAbbreviatedString((diff / 1000L).toInt())
        }
        return null
    }

    fun getTimerScore(time: Long): String? {
        val diff = time - System.currentTimeMillis()
        return if (diff > 0) {
            TimeUtils.formatIntoAbbreviatedString((diff / 1000L).toInt())
        } else {
            null
        }
    }
}