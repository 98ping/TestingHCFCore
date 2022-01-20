package me.ninetyeightping.hcf.board

import io.github.thatkawaiisam.assemble.AssembleAdapter
import io.github.thatkawaiisam.assemble.AssembleBoard
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
        if (HCF.instance.timerHandler.enderpearlTimer.hasCooldown(player!!)) {
            lines.add("&9Enderpearl: &f" + getPearlScore(player))
        }
        lines.add("&c&7&m--------------------")
        return lines;
    }

    fun getPearlScore(player: Player?) : String? {
        val diff = HCF.instance.timerHandler.enderpearlTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoMMSS((diff / 1000L).toInt())
        }
        return null
    }
}