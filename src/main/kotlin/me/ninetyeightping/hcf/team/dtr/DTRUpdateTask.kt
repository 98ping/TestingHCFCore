package me.ninetyeightping.hcf.team.dtr

import me.ninetyeightping.hcf.HCF
import org.bukkit.scheduler.BukkitRunnable

class DTRUpdateTask : BukkitRunnable() {

    override fun run() {
        for (team in HCF.instance.teamHandler.teams) {

            val diff = team.dtrregen - System.currentTimeMillis()
            if (diff < 0) {
                team.dtr = team.calculateMaximumDTR()
                team.dtrregen = 0L

                team.save()
            }
        }
    }
}