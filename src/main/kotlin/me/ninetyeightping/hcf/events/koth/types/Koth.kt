package me.ninetyeightping.hcf.events.koth.types

import me.ninetyeightping.hcf.HCF
import me.ninetyeightping.hcf.util.Chat
import me.ninetyeightping.hcf.util.Cuboid
import me.ninetyeightping.hcf.util.TimeUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

data class Koth(var name: String,
                var isActive: Boolean,
                var duration: Int,
                var loc1: Location?,
                var loc2: Location?){


    var activeTimer = duration
    var someoneIsOnKoth = false

    fun getKothTimer() : Int {
        return activeTimer
    }

    fun reset() {
        activeTimer = duration
    }

    fun save() {
        HCF.instance.kothHandler.save(this)
    }

    fun constructCuboid() : Cuboid {
        return Cuboid(loc1, loc2)
    }


    fun activate() {
        isActive = true
        Bukkit.broadcastMessage(Chat.format("&c[Koth] &f" + name + " &7has just started &c(" + TimeUtils.formatIntoMMSS(duration) + ")"))
        object : BukkitRunnable() {
            override fun run() {

                if (HCF.instance.kothHandler.playerIsStandingInKothRegion(this@Koth)) {


                    someoneIsOnKoth = true
                    when (activeTimer) {


                        120 -> {
                            Bukkit.broadcastMessage(Chat.format("&c[Koth] &f2 minutes &7left on the &f$name &7koth"))
                        }

                        60 -> {
                            Bukkit.broadcastMessage(Chat.format("&c[Koth] &f1 minute &7left on the &f$name &7koth"))
                        }

                        30 -> {
                            Bukkit.broadcastMessage(Chat.format("&c[Koth] &f30 seconds &7left on the &f$name &7koth"))
                        }

                        0 -> {
                            val firstwonplayer = Bukkit.getOnlinePlayers().stream().filter { this@Koth.constructCuboid().contains(it.location) }.findFirst().orElse(null)
                            if (firstwonplayer != null) { giveKothReward(firstwonplayer) }
                            deactivate()
                        }
                    }

                    activeTimer--

                }

                if (!HCF.instance.kothHandler.playerIsStandingInKothRegion(this@Koth) && someoneIsOnKoth) {
                    reset()
                    someoneIsOnKoth = false
                }

                if (!isActive) {
                    cancel()
                }


            }

        }.runTaskTimer(HCF.instance, 0L, 20L)
    }

    fun giveKothReward(player: Player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HCF.instance.config.getString("koth-won-command"))
    }

    fun deactivate() {
        isActive = false
        Bukkit.broadcastMessage(Chat.format("&c[Koth] &f$name &7has just ended"))
        reset()
    }
}