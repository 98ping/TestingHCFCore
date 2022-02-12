package me.ninetyeightping.hcf.pvpclass.types.tasks

import me.ninetyeightping.hcf.pvpclass.types.Archer
import me.ninetyeightping.hcf.team.TeamHandler
import me.ninetyeightping.hcf.util.InjectionUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class ArcherCheckTask : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {

            if (Archer.energyMap.getOrDefault(player.uniqueId, 0) < 100) {
                if (Archer.isInArcherClass(player)) {
                    Archer.energyMap[player.uniqueId] = Archer.energyMap.getOrDefault(player.uniqueId, 0) + 1
                }
            }
            if (Archer.archerClassNeedsRemoval(player)) {
                Archer.onRemoval(player)
            }
            if (Archer.hasArmorOn(player) && !Archer.listOfArchers.contains(player.uniqueId)) {
                Archer.onEquip(player)
            }

        }
    }
}