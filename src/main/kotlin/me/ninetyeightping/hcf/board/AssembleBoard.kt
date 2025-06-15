package me.ninetyeightping.hcf.board

import io.github.thatkawaiisam.assemble.AssembleAdapter
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.players.HCFPlayerHandler
import me.ninetyeightping.hcf.pvpclass.types.Archer
import me.ninetyeightping.hcf.pvpclass.types.Bard
import me.ninetyeightping.hcf.timers.impl.CombatTimer
import me.ninetyeightping.hcf.timers.impl.EffectCooldownTimer
import me.ninetyeightping.hcf.timers.impl.EnderpearlTimer
import me.ninetyeightping.hcf.timers.impl.FHomeTimer
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.TimeUtils
import org.bukkit.entity.Player

class AssembleBoard : AssembleAdapter {

    override fun getTitle(player: Player?): String {
        return Chat.format(HCF.instance.config.getString("scoreboard.title"))
    }

    override fun getLines(player: Player?): MutableList<String> {
        val lines = arrayListOf<String>()
        lines.add("&7&m--------------------")

        val hcfplayer = HCF.instance.playerHandler.byPlayer(player!!)
        if (hcfplayer != null) {
            val entry = hcfplayer.stats
            if (HCF.instance.config.getBoolean("kits")) {
                lines.add("&3Kills&7: &f" + entry.kills)
                lines.add("&3Deaths&7: &f" + entry.deaths)
                lines.add("&3Killstreak&7: &f" + entry.killstreak)
            }
        }
        lines.add("&6&lClaim: &r" + HCF.instance.landBoard.getClaimForScoreboard(player))

        if (EnderpearlTimer.hasCooldown(player)) {
            lines.add("&9&lEnderpearl: &f" + getPearlScore(player))
        }

        if (FHomeTimer.hasCooldown(player)) {
            lines.add("&e&lHome: &f" + getHomeScore(player))
        }

        if (CombatTimer.hasCooldown(player)) {
            lines.add("&c&lCombat: &f" + getCombatScore(player))
        }
        if (Archer.isInArcherClass(player)) {
            lines.add("&eActive Class: &5Archer")
            lines.add("&7* &6Archer Energy: &f" + Archer.energyMap.getOrDefault(player.uniqueId, 0))
            if (EffectCooldownTimer.hasCooldown(player)) {
                lines.add("&7* &6Effect: &f" + getEffectScore(player))
            }
        }

        if (Bard.isInBardClass(player)) {
            lines.add("&eActive Class: &6Bard")
            lines.add("&7* &6Energy: &f" + Bard.energyMap.getOrDefault(player.uniqueId, 0))
            if (EffectCooldownTimer.hasCooldown(player)) {
                lines.add("&7* &6Effect: &f" + getEffectScore(player))
            }
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
            CombatTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoMMSS((diff / 1000L).toInt())
        }
        return null
    }

    fun getHomeScore(player: Player?): String? {
        val diff =
            FHomeTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoMMSS((diff / 1000L).toInt())
        }
        return null
    }

    fun getEffectScore(player: Player?): String? {
        val diff =
            EffectCooldownTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoMMSS((diff / 1000L).toInt())
        }
        return null
    }

    fun getPearlScore(player: Player?): String? {
        val diff =
            EnderpearlTimer.cooldownMap[player!!.uniqueId]?.minus(System.currentTimeMillis())
        if (diff!! > 0) {
            return TimeUtils.formatIntoMMSS((diff / 1000L).toInt())
        }
        return null
    }

    fun getTimerScore(time: Long): String? {
        val diff = time - System.currentTimeMillis()
        return if (diff > 0) {
            TimeUtils.formatIntoMMSS((diff / 1000L).toInt())
        } else {
            null
        }
    }
}