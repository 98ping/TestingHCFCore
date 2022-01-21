package me.ninetyeightping.hcf.board

import io.github.thatkawaiisam.assemble.AssembleAdapter
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.players.stat.StatisticEntry
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.InjectionUtil
import me.ninetyeightping.hcf.util.TimeUtils
import org.bukkit.entity.Player

class AssembleBoard : AssembleAdapter {

    override fun getTitle(player: Player?): String {
        return Chat.format("&c&lTesting")
    }

    override fun getLines(player: Player?): MutableList<String> {
        val lines = arrayListOf<String>()
        lines.add("&7&m--------------------")

        val hcfplayer = InjectionUtil.get(HCFPlayerHandler::class.java).byPlayer(player!!)
        if (hcfplayer != null) {
            val entry = hcfplayer.stats
            if (HCF.instance.config.getBoolean("kits")) {
                lines.add("&eKills&7: &f" + entry.kills)
                lines.add("&eDeaths&7: &f" + entry.deaths)
                lines.add("&eKillstreak&7: &f" + entry.killstreak)
            }
        }
        lines.add("&6&lClaim: &r" + HCF.instance.landBoard.getClaimForScoreboard(player!!))

        if (HCF.instance.timerHandler.enderpearlTimer.hasCooldown(player)) {
            lines.add("&9Enderpearl: &f" + getPearlScore(player))
        }

        if (HCF.instance.timerHandler.combatTimer.hasCooldown(player)) {
            lines.add("&4Combat: &f" + getCombatScore(player))
        }


        if (HCF.instance.sotwHandler.serverIsOnSOTWTimer()) {
            if (!HCF.instance.sotwHandler.isSOTWEnabled(player)) {
                lines.add("&a&lSOTW: " + getTimerScore(HCF.instance.sotwHandler.globalDuration))
            } else {
                lines.add("&a&l&mSOTW: " + getTimerScore(HCF.instance.sotwHandler.globalDuration))
            }
        }

        if (HCF.instance.kothHandler.serverHasActiveKoth()) {
            val koth = HCF.instance.kothHandler.getFirstActiveKoth()
            lines.add("&c&l" + koth!!.name + "&7: &f" + TimeUtils.formatIntoMMSS(koth.getKothTimer()))
        }
        lines.add("&c&7&m--------------------")
        return lines;
    }

    fun getCombatScore(player: Player?): String? {
        val diff =
            HCF.instance.timerHandler.combatTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoAbbreviatedString((diff / 1000L).toInt())
        }
        return null
    }

    fun getPearlScore(player: Player?): String? {
        val diff =
            HCF.instance.timerHandler.enderpearlTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
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