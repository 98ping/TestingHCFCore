package me.ninetyeightping.hcf.timers

import me.ninetyeightping.hcf.timers.impl.CombatTimer
import me.ninetyeightping.hcf.timers.impl.EnderpearlTimer

class TimerHandler {

    lateinit var enderpearlTimer: EnderpearlTimer
    lateinit var combatTimer: CombatTimer

    init {
        enderpearlTimer = EnderpearlTimer
        combatTimer = CombatTimer
    }
}