package me.ninetyeightping.hcf.util

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.util.*
import java.util.stream.Collectors


class ItemBuilder {
    private val item: ItemStack

    private constructor(material: Material, amount: Int) {
        Preconditions.checkArgument(amount > 0, "Amount cannot be lower than 0.")
        item = ItemStack(material, amount)
    }

    private constructor(item: ItemStack) {
        this.item = item
    }

    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun data(data: Short): ItemBuilder {
        item.durability = data
        return this
    }

    fun enchant(enchantment: Enchantment?, level: Int): ItemBuilder {
        item.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun unenchant(enchantment: Enchantment?): ItemBuilder {
        item.removeEnchantment(enchantment)
        return this
    }

    fun name(displayName: String?): ItemBuilder {
        val meta = item.itemMeta
        meta.displayName =
            if (displayName == null) null else ChatColor.translateAlternateColorCodes('&', displayName)
        item.itemMeta = meta
        return this
    }



    fun setLore(l: Collection<String>): ItemBuilder {
        val meta = item.itemMeta
        meta.lore = l as MutableList<String>
        item.itemMeta = meta
        return this
    }

    fun color(color: Color?): ItemBuilder {
        val meta = item.itemMeta as? LeatherArmorMeta
            ?: throw UnsupportedOperationException("Cannot set color of a non-leather armor item.")
        meta.color = color
        item.itemMeta = meta
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        val meta = item.itemMeta
        meta.spigot().isUnbreakable = unbreakable
        item.itemMeta = meta
        return this
    }

    fun build(): ItemStack {
        return item.clone()
    }

    companion object {
        fun of(material: Material): ItemBuilder {
            return ItemBuilder(material, 1)
        }

        fun of(material: Material, amount: Int): ItemBuilder {
            return ItemBuilder(material, amount)
        }

        fun copyOf(builder: ItemBuilder): ItemBuilder {
            return ItemBuilder(builder.build())
        }

        fun copyOf(item: ItemStack): ItemBuilder {
            return ItemBuilder(item)
        }
    }
}