package me.ninetyeightping.hcf.pvpclass.types.tasks

import me.ninetyeightping.hcf.pvpclass.types.Bard
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class BardCheckTask : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {

            if (Bard.energyMap.getOrDefault(player.uniqueId, 0) < 100) {
                Bard.energyMap[player.uniqueId] = Bard.energyMap.getOrDefault(player.uniqueId, 0) + 1
            }
            if (Bard.bardClassNeedsRemoval(player)) {
                Bard.onRemoval(player)
            }
            if (Bard.hasArmorOn(player) && !Bard.listOfBards.contains(player.uniqueId)) {
                Bard.onEquip(player)
            }

        }
    }
}