package me.ninetyeightping.hcf.team.dtr

import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.util.InjectionUtil
import org.bukkit.scheduler.BukkitRunnable

class DTRUpdateTask : BukkitRunnable() {

    override fun run() {
        for (team in InjectionUtil.get(TeamHandler::class.java).teams) {

            if (team.dtrregen != 0L && System.currentTimeMillis() <= team.dtrregen) {
                team.dtr = team.calculateMaximumDTR()
                team.dtrregen = 0L

                team.save()
            }
        }
    }
}