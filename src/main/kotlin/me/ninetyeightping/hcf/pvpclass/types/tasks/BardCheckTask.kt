package me.ninetyeightping.hcf.pvpclass.types.tasks

import me.ninetyeightping.hcf.pvpclass.types.Bard
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class BardCheckTask : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (Bard.bardClassNeedsRemoval(player)) {
                Bard.onRemoval(player)
            }
        }
    }
}