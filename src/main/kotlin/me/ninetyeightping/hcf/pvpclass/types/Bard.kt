package me.ninetyeightping.hcf.pvpclass.types

import com.google.common.collect.HashBasedTable
import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.pvpclass.PvPClass
import me.ninetyeightping.hcf.pvpclass.PvPClassType
import me.ninetyeightping.hcf.pvpclass.types.effects.BardEffect
import me.ninetyeightping.hcf.timers.impl.EffectCooldownTimer
import me.ninetyeightping.hcf.util.Chat
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object Bard : PvPClass("Bard Class", PvPClassType.BARD), Listener {

    var listOfBards = arrayListOf<UUID>()
    var effectRestoreTable = HashBasedTable.create<UUID, PotionEffectType, PotionEffect>()
    var energyMap = hashMapOf<UUID, Int>()
    var effectMap = hashMapOf<Material, BardEffect>()

    fun loadItems() {
        effectMap[Material.SUGAR] = BardEffect(25, PotionEffect(PotionEffectType.SPEED, (5 * 20), 2))
        effectMap[Material.BLAZE_POWDER] = BardEffect(45, PotionEffect(PotionEffectType.SPEED, (5 * 20), 1))
        effectMap[Material.IRON_INGOT] = BardEffect(30, PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (5 * 20), 2))
        effectMap[Material.MAGMA_CREAM] = BardEffect(20, PotionEffect(PotionEffectType.FIRE_RESISTANCE, (5 * 20), 0))
        effectMap[Material.FEATHER] = BardEffect(25, PotionEffect(PotionEffectType.JUMP, (5 * 20), 4))
    }

    fun bardClassNeedsRemoval(player: Player) : Boolean {
        return !hasArmorOn(player) && listOfBards.contains(player.uniqueId)
    }

    fun isInBardClass(player: Player) : Boolean {
        return listOfBards.contains(player.uniqueId)
    }

    override fun onEquip(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0))
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0))
        player.sendMessage(Chat.format("&aEquipped the &6Bard &aclass"))
        listOfBards.add(player.uniqueId)
    }

    fun applyBardEffect(player: Player, effect: PotionEffect, energy: Int) {
        for (potionEffect in player.activePotionEffects) {
            effectRestoreTable.put(player.uniqueId, potionEffect.type, potionEffect)
        }
        player.removePotionEffect(effect.type)
        energyMap[player.uniqueId] = energyMap.getOrDefault(player.uniqueId, 0) - energy
        player.addPotionEffect(effect)
        Bukkit.getScheduler().runTaskLater(HCF.instance, {
            val potioneffect = effectRestoreTable.remove(player.uniqueId, effect.type)

            if (potioneffect == null) return@runTaskLater

            player.addPotionEffect(potioneffect)
            println("Restored potion effect for " + player.name + " type: " + potioneffect.type)

        }, (effect.duration).toLong())
    }

    fun ensurePlayerCanUseEffect(player: Player, energy: Int) : Boolean {
        return energyMap.getOrDefault(player.uniqueId, 0) > energy
    }

    override fun hasArmorOn(player: Player): Boolean {
        if (player.inventory.helmet == null
            || player.inventory.chestplate == null
            || player.inventory.leggings == null
            || player.inventory.boots == null) {
            return  false
        }
        val helmet = player.inventory.helmet.type
        val chestie = player.inventory.chestplate.type
        val legs = player.inventory.leggings.type
        val boots = player.inventory.boots.type

        return (helmet != null && helmet == Material.GOLD_HELMET
                && chestie != null && chestie == Material.GOLD_CHESTPLATE
                && legs != null && legs == Material.GOLD_LEGGINGS
                && boots != null && boots == Material.GOLD_BOOTS)
    }

    override fun onRemoval(player: Player) {
        player.removePotionEffect(PotionEffectType.SPEED)
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
        player.removePotionEffect(PotionEffectType.REGENERATION)
        player.sendMessage(Chat.format("&cRemoved the &6Bard &cclass"))
        listOfBards.remove(player.uniqueId)
        energyMap.remove(player.uniqueId)
    }


    //listener

    @EventHandler
    fun useEffect(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {

            if (player.itemInHand != null) {

                val materialOfItemInHand = player.itemInHand.type

                val bardEffectByItemInHand = effectMap.getOrDefault(materialOfItemInHand, null) ?: return

                if (!isInBardClass(player)) return

                if (!ensurePlayerCanUseEffect(player, bardEffectByItemInHand.energy)) {
                    player.sendMessage(Chat.format("&cYou lack the required energy to use this buff. You need &l" + bardEffectByItemInHand.energy))
                    return
                }

                if (EffectCooldownTimer.hasCooldown(player)) {
                    player.sendMessage(Chat.format("&cYou are currently on effect colodown"))
                    return
                }

                applyBardEffect(player, bardEffectByItemInHand.potionEffect, bardEffectByItemInHand.energy)
                EffectCooldownTimer.addCooldown(player)

                if (player.itemInHand.amount > 1) {
                    player.itemInHand.amount = player.itemInHand.amount - 1
                } else {
                    player.itemInHand = null
                }
                player.updateInventory()

                player.getNearbyEntities(10.0, 10.0, 10.0).stream().filter { it is Player }.forEach {
                    val player = it as Player

                    applyBardEffect(player, bardEffectByItemInHand.potionEffect, bardEffectByItemInHand.energy)
                }
            }
        }
    }
}