package me.ninetyeightping.hcf.pvpclass.types.tasks

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.pvpclass.types.Bard
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class BardCheckTask : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {

            if (Bard.energyMap.getOrDefault(player.uniqueId, 0) < 100) {
                if (Bard.isInBardClass(player)) {
                    Bard.energyMap[player.uniqueId] = Bard.energyMap.getOrDefault(player.uniqueId, 0) + 1
                }
            }
            if (Bard.bardClassNeedsRemoval(player)) {
                Bard.onRemoval(player)
            }
            if (Bard.hasArmorOn(player) && !Bard.listOfBards.contains(player.uniqueId)) {
                Bard.onEquip(player)
            }

            if (Bard.effectMap.getOrDefault(player.itemInHand.type, null) != null) {
                val effect = Bard.effectMap.getOrDefault(player.itemInHand.type, null)

                player.addPotionEffect(effect!!.potionEffect)

                player.getNearbyEntities(10.0, 10.0, 10.0).stream().filter { it is Player }.forEach {
                    val team = HCF.instance.teamHandler.byPlayer(player)
                    val teamOther = HCF.instance.teamHandler.byPlayer(it as Player)

                    if (team == teamOther) {
                        it.addPotionEffect(effect.potionEffect)
                    }
                }
            }

        }
    }
}