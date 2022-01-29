package me.ninetyeightping.hcf.events.sotw

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class SOTWHandler {

    var sotwMap = hashMapOf<UUID, Boolean>()

    var globalDuration = 0L

    fun enableSotwTimer(player: Player) {
        sotwMap[player.uniqueId] = true
    }

    fun startSOTWTimer(duration: Int) {
        globalDuration = System.currentTimeMillis() + (duration * 1000)
    }

    fun serverIsOnSOTWTimer() : Boolean {
       return System.currentTimeMillis() <= globalDuration
    }

    fun isSOTWEnabled(player: Player) : Boolean {
        return sotwMap.getOrDefault(player.uniqueId, false)
    }
}