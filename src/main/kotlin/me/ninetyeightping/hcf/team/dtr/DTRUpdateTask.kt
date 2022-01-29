package me.ninetyeightping.hcf.team.dtr

import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.util.InjectionUtil
import me.ninetyeightping.hcf.util.TimeUtils
import org.bukkit.scheduler.BukkitRunnable

class DTRUpdateTask : BukkitRunnable() {

    override fun run() {
        for (team in InjectionUtil.get(TeamHandler::class.java).teams) {

            val diff = team.dtrregen - System.currentTimeMillis()
            if (diff < 0) {
                team.dtr = team.calculateMaximumDTR()
                team.dtrregen = 0L

                team.save()
            }
        }
    }
}