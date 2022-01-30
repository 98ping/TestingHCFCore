package me.ninetyeightping.hcf.pvpclass.types

import com.google.common.collect.HashBasedTable
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.pvpclass.PvPClass
import me.ninetyeightping.hcf.pvpclass.PvPClassType
import me.ninetyeightping.hcf.util.Chat
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object Bard : PvPClass("Bard Class", PvPClassType.BARD) {

    var listOfBards = arrayListOf<UUID>()
    var effectRestoreTable = HashBasedTable.create<UUID, PotionEffectType, PotionEffect>()
    var energyMap = hashMapOf<UUID, Int>()

    fun bardClassNeedsRemoval(player: Player) : Boolean {
        return !hasArmorOn(player) && listOfBards.contains(player.uniqueId)
    }

    override fun onEquip(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0))
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0))
        player.sendMessage(Chat.format("&aEquipped the &6Bard &aclass"))
        listOfBards.add(player.uniqueId)
    }

    fun applyBardEffect(player: Player, effect: PotionEffect) {
        for (potionEffect in player.activePotionEffects) {
            effectRestoreTable.put(player.uniqueId, potionEffect.type, potionEffect)
        }
        player.addPotionEffect(effect)
        Bukkit.getScheduler().runTaskLater(HCF.instance, {
            val potioneffect = effectRestoreTable.get(player.uniqueId, effect.type) ?: return@runTaskLater


            player.addPotionEffect(potioneffect)

        }, (effect.duration).toLong())
    }

    fun ensurePlayerCanUseEffect(player: Player, energy: Int) : Boolean {
        return energyMap.getOrDefault(player.uniqueId, 0) > energy
    }

    override fun hasArmorOn(player: Player): Boolean {
        return player.inventory.helmet.type == Material.GOLD_HELMET
                && player.inventory.chestplate.type == Material.GOLD_CHESTPLATE
                && player.inventory.leggings.type == Material.GOLD_LEGGINGS
                && player.inventory.boots.type == Material.GOLD_BOOTS
    }

    override fun onRemoval(player: Player) {
        player.removePotionEffect(PotionEffectType.SPEED)
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
        player.removePotionEffect(PotionEffectType.REGENERATION)
        player.sendMessage(Chat.format("&cRemoved the &6Bard &cclass"))
        listOfBards.remove(player.uniqueId)
        energyMap.remove(player.uniqueId)
    }
}